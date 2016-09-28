/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Request;
import spark.Session;
import spark.TemplateEngine;
import tikape.keskustelufoorumi.MyTemplate;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;
import tikape.keskustelufoorumi.database.Database;
import tikape.keskustelufoorumi.database.OpiskelijaDao;
import tikape.keskustelufoorumi.domain.Alue;
import tikape.keskustelufoorumi.domain.Opiskelija;
import tikape.keskustelufoorumi.validator.MaxLengthRule;
import tikape.keskustelufoorumi.validator.MinLengthRule;
import tikape.keskustelufoorumi.validator.PatternRule;
import tikape.keskustelufoorumi.validator.Validator;
import static spark.Spark.get;
import static spark.Spark.post;
import tikape.keskustelufoorumi.Context;
import tikape.keskustelufoorumi.database.AccessTokenDao;
import tikape.keskustelufoorumi.domain.AccessToken;
import tikape.keskustelufoorumi.validator.EqualsRule;

public class WebUI implements UI {
    private Database database;
    private OpiskelijaDao opiskelijaDao;
    private AccessTokenDao accessTokenDao;
    
    private Menu menu;
    
    public WebUI(Database database) {
        this.database = database;
    }
    
    public void init() throws SQLException {
        this.opiskelijaDao = new OpiskelijaDao(this.database);
        this.accessTokenDao = new AccessTokenDao(this.database);
        
        int port = 4567;
        
        //herokua varten
        if(System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        }
        
        port(port);
        staticFileLocation("/public");
        
        this.menu = new Menu();
        this.menu.addItem("home", "Etusivu", "/");
        this.menu.addItem("login", "Kirjaudu", "/kirjaudu", (Context ctx) -> {
            return ctx.getLoggedInUser() == null;
        });
        this.menu.addItem("register", "Rekisteröidy", "/rekisteroidy", (Context ctx) -> {
            return ctx.getLoggedInUser() == null;
        });
        this.menu.addItem("logout", "Kirjaudu ulos", "/ulos", (Context ctx) -> {
            return ctx.getLoggedInUser() != null;
        });
    }
    
    private Integer extractId(String text) {
        try {
            String[] parts = text.split("-");
            Integer id = Integer.parseInt(parts[0]);
            return id;
        } catch(Exception e) {
            return null;
        }
    }
    
    private Context getContext(Request req) {
        Context ctx = new Context();
        HashMap map = new HashMap<>();
        
        ctx.setMap(map);
        
        if(req.cookies().containsKey("access_token")) {
            AccessToken token = this.accessTokenDao.findOneBy("token", req.cookies().get("access_token"));
            
            if(token != null) {
                Opiskelija opiskelija = this.opiskelijaDao.findOne(token.getOpiskelijaId());
                
                ctx.setAccessToken(token);
                ctx.setLoggedInUser(opiskelija);
                
                map.put("user", ctx.getLoggedInUser());
            }
        }
        
        //ctx.setLoggedInUser(this.opiskelijaDao.findOne(5));
        //map.put("user", ctx.getLoggedInUser());
        
        Session s = req.session();
        
        map.put("success", s.attribute("success"));
        map.put("error", s.attribute("error"));
        map.put("info", s.attribute("info"));
        map.put("warning", s.attribute("warning"));
        
        s.attribute("success", null);
        s.attribute("error", null);
        s.attribute("info", null);
        s.attribute("warning", null);
        
        Menu menu = this.menu.buildWithContext(ctx);
        
        ctx.setMenu(menu);
        map.put("menu", menu);
        
        return ctx;
    }

    public void start() {
        TemplateEngine engine = new MyTemplate();
        
        get("/", (req, res) -> {            
            List<Alue> alueet = new ArrayList();
            alueet.add(new Alue(1, "perunakellarien iltahuvit"));
            alueet.add(new Alue(2, "tomaattien maailmanvalloitus"));
            alueet.add(new Alue(3, "kurkkusalaattien maihinnousu"));
            
            Context ctx = getContext(req);
            ctx.getMenu().setActive("home");
            
            HashMap map = ctx.getMap();

            map.put("viesti", "tervehdys");
            map.put("alueet", alueet);
            
            return new ModelAndView(map, "index");
        }, engine);
        
        get("/alue/:id", (req, res) -> {
            return extractId(req.params(":id"));
        });
        
        get("/ulos", (req, res) -> {
            Context ctx = getContext(req);
            
            if(ctx.getAccessToken() != null) {
                this.accessTokenDao.delete(ctx.getAccessToken().getId());
                
                res.removeCookie("access_token");
                req.session().attribute("success", "Uloskirjautuminen onnistui");
                res.redirect("/");
            }
            
            return null; 
        });
        
        get("/kirjaudu", (req, res) -> {
            Context ctx = getContext(req);
            ctx.getMenu().setActive("login");
            
            HashMap map = ctx.getMap();
            
            map.put("login-name", req.session().attribute("login-name"));
            req.session().attribute("login-name", null);
            
            return new ModelAndView(map, "kirjaudu");
        }, engine);
        
        post("/kirjaudu", (req, res) -> {
            if(req.queryParams("login-ok") != null) {
                String name = req.queryParams("login-name");
                String pw = req.queryParams("login-pw");
                
                Opiskelija o = (Opiskelija)this.opiskelijaDao.findOneBy("nimi", name);
                if(o == null || !o.passwordMatches(pw)) {
                    req.session().attribute("login-name", name);
                    req.session().attribute("error", "Kirjautuminen epäonnistui: virheellinen käyttäjätunnus tai salasana");
                    res.redirect("/kirjaudu");
                } else {
                    String token = Opiskelija.generateAcccessToken();
                    this.accessTokenDao.insert(token, o.getId());
                    
                    res.cookie("access_token", token, 60 * 60 * 24 * 7);
                    req.session().attribute("success", "Kirjautuminen onnistui");
                    res.redirect("/");
                }
            }
            
            return null;
        });
        
        get("/rekisteroidy", (req, res) -> {
            Context ctx = getContext(req);
            ctx.getMenu().setActive("register");
            
            HashMap map = ctx.getMap();
            
            map.put("register-name", req.session().attribute("register-name"));
            req.session().attribute("register-name", null);
            
            return new ModelAndView(map, "rekisteroidy");
        }, engine);
        
        post("/rekisteroidy", (req, res) -> {
            if(req.queryParams("register-ok") != null) {
                String name = req.queryParams("register-name");
                String pw = req.queryParams("register-pw");
                String pw2 = req.queryParams("register-pw2");
                
                req.session().attribute("register-name", name);
                
                Validator nameValidator = new Validator();
                nameValidator.addRule(new MinLengthRule(3, "nimen pitää olla yli 3 merkkiä"));
                nameValidator.addRule(new MaxLengthRule(20, "nimi ei saa olla yli 20 merkkiä pitkä"));
                nameValidator.addRule(new PatternRule("^[a-zA-Z0-9]+$", "nimi saa sisältää vain kirjaimia ja numeroita"));
                
                Validator pwValidator = new Validator();
                pwValidator.addRule(new MinLengthRule(8, "salasanan pitää olla vähintään 8 merkkiä pitkä"));
                pwValidator.addRule(new EqualsRule(pw2, "salasanat eivät ole samoja"));
                
                String error = null;
                
                Opiskelija o = (Opiskelija)this.opiskelijaDao.findOneBy("nimi", name);
                
                if(o != null) {
                    error = "nimi on jo käytössä";
                } else if(!nameValidator.validate(name)) {
                    error = nameValidator.getReason();
                } else if(!pwValidator.validate(pw)) {
                    error = pwValidator.getReason();
                }
                
                if(error == null) {
                    try {
                        opiskelijaDao.insert(name, pw);
                    } catch(Exception e) {
                        error = "tuntematon syy";
                    }
                }
                
                if(error != null) {
                    req.session().attribute("error", "Rekisteröityminen epäonnistui: " + error);
                    res.redirect("/rekisteroidy");
                    return null;
                }
                
                req.session().attribute("success", "Rekisteröityminen onnistui");

                res.redirect("/");
            }
                
            return null;
        });

        get("/opiskelijat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelijat", opiskelijaDao.findAll());

            return new ModelAndView(map, "opiskelijat");
        }, engine);

        get("/opiskelijat/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", opiskelijaDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "opiskelija");
        }, engine);
    }
}
