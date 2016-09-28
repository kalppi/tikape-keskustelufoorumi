/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.ui;

import tikape.keskustelufoorumi.ui.UI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Request;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;
import spark.TemplateEngine;
import tikape.keskustelufoorumi.MyTemplate;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;
import tikape.keskustelufoorumi.database.Dao;
import tikape.keskustelufoorumi.database.Database;
import tikape.keskustelufoorumi.database.OpiskelijaDao;
import tikape.keskustelufoorumi.domain.Alue;
import tikape.keskustelufoorumi.validator.MaxLengthRule;
import tikape.keskustelufoorumi.validator.MinLengthRule;
import tikape.keskustelufoorumi.validator.PatternRule;
import tikape.keskustelufoorumi.validator.Validator;

public class WebUI implements UI {
    private Database database;
    private Dao opiskelijaDao;
    
    private Menu menu;
    
    public WebUI(Database database) {
        this.database = database;
    }
    
    public void init() throws SQLException {
        this.opiskelijaDao = new OpiskelijaDao(this.database);
        
        int port = 4567;
        
        //herokua varten
        if(System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        }
        
        port(port);
        staticFileLocation("/public");
        
        this.menu = new Menu();
        this.menu.addItem("home", "Etusivu", "/");
        this.menu.addItem("login", "Kirjaudu", "/kirjaudu");
        this.menu.addItem("register", "Rekisteröidy", "/rekisteroidy");
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
    
    private HashMap getDefaultMap(Request req, String activeMenu) {
        HashMap map = new HashMap<>();
        
        this.menu.setActive(activeMenu);
        
        map.put("menu", this.menu);
        map.put("success", req.session().attribute("success"));
        map.put("error", req.session().attribute("error"));
        
        req.session().attribute("success", null);
        req.session().attribute("error", null);
        
        return map;
    }
    
    public void start() {
        TemplateEngine engine = new MyTemplate();
        
        get("/", (req, res) -> {            
            List<Alue> alueet = new ArrayList();
            alueet.add(new Alue(1, "perunakellarien iltahuvit"));
            alueet.add(new Alue(2, "tomaattien maailmanvalloitus"));
            alueet.add(new Alue(3, "kurkkusalaattien maihinnousu"));
            
            HashMap map = getDefaultMap(req, "home");

            map.put("viesti", "tervehdys");
            map.put("alueet", alueet);
            
            return new ModelAndView(map, "index");
        }, engine);
        
        get("/alue/:id", (req, res) -> {
            return extractId(req.params(":id"));
        });
        
        get("/kirjaudu", (req, res) -> {
            HashMap map = getDefaultMap(req, "login");
            
            return new ModelAndView(map, "kirjaudu");
        }, engine);
        
        get("/rekisteroidy", (req, res) -> {
            HashMap map = getDefaultMap(req, "register");
            
            return new ModelAndView(map, "rekisteroidy");
        }, engine);
        
        post("/rekisteroidy", (req, res) -> {
            if(req.queryParams("register-ok") != null) {
                String name = req.queryParams("register-name");
                String pw = req.queryParams("register-pw");
                
                req.session().attribute("register-name", name);
                
                Validator registrationValidator = new Validator();
                
                Validator nameValidator = new Validator();
                nameValidator.addRule(new MinLengthRule(3, "nimen pitää olla yli 3 merkkiä"));
                nameValidator.addRule(new MaxLengthRule(20, "nimi ei saa olla yli 20 merkkiä pitkä"));
                nameValidator.addRule(new PatternRule("^[a-zA-Z]+$", "nimi saa sisältää vain kirjaimia"));
                
                Validator pwValidator = new Validator();
                
                registrationValidator.addRule(nameValidator);
                registrationValidator.addRule(pwValidator);
                
                if(!registrationValidator.validate(name)) {
                    req.session().attribute("error", "Rekisteröityminen epäonnistui: " + registrationValidator.getReason());
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
