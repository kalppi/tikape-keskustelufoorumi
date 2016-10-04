package tikape.keskustelufoorumi.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
import static spark.Spark.before;
import spark.TemplateEngine;
import tikape.keskustelufoorumi.MyTemplate;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;
import tikape.keskustelufoorumi.database.Database;
import tikape.keskustelufoorumi.database.UserDao;
import tikape.keskustelufoorumi.domain.Category;
import tikape.keskustelufoorumi.domain.User;
import tikape.keskustelufoorumi.validator.*;
import tikape.keskustelufoorumi.Auth;
import tikape.keskustelufoorumi.Context;
import tikape.keskustelufoorumi.database.AccessTokenDao;
import tikape.keskustelufoorumi.domain.AccessToken;
import tikape.keskustelufoorumi.validator.EqualsRule;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import spark.TemplateViewRoute;
import tikape.keskustelufoorumi.database.CategoryDao;
import tikape.keskustelufoorumi.database.MessageDao;
import tikape.keskustelufoorumi.domain.Message;

public class WebUI implements UI {
    private Database database;
    private UserDao userDao;
    private CategoryDao categoryDao;
    private MessageDao messageDao;
    
    private AccessTokenDao accessTokenDao;
    
    private Menu menu;
    
    public WebUI(Database database) {
        this.database = database;
    }
    
    @Override
    public void init() throws SQLException {
        this.userDao = new UserDao(this.database);
        this.accessTokenDao = new AccessTokenDao(this.database);
        this.categoryDao = new CategoryDao(this.database);
        this.messageDao = new MessageDao(this.database);
        
        int port = 4567;
        
        //herokua varten
        if(System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        }
        
        port(port);
        staticFileLocation("/public");
        
        this.menu = new Menu();
        this.menu.addItem("home", "Etusivu", "/");
        this.menu.addItem("users", "Käyttäjät", "/kayttajat");
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
            return 0;
        }
    }
    
    private Integer extractPage(String text) {
        try {
            Integer id = Integer.parseInt(text.replace("sivu-", ""));
            return id;
        } catch(Exception e) {
            return 0;
        }
    }
    
    private Context getContext(Request req, Response res) {
        Context ctx = new Context();
        HashMap map = new HashMap<>();
        
        ctx.setMap(map);
        
        /*
        
        mikäli löytyy käytössä oleva access_token keksi, kirjataan käyttäjä sisään
        
        */
        if(req.cookies().containsKey("access_token")) {
            AccessToken token = this.accessTokenDao.findOneBy("token", req.cookies().get("access_token"));
            
            if(token != null) {
                User user = this.userDao.findOne(token.getUserId());
                
                ctx.setAccessToken(token);
                ctx.setLoggedInUser(user);
                
                map.put("user", user);
            }
        }
        
        Session s = req.session();
        
        map.put("success", s.attribute("success"));
        map.put("error", s.attribute("error"));
        map.put("info", s.attribute("info"));
        map.put("warning", s.attribute("warning"));
        
        s.attribute("success", null);
        s.attribute("error", null);
        s.attribute("info", null);
        s.attribute("warning", null);
        
        Menu userMenu = this.menu.buildWithContext(ctx);
        
        ctx.setMenu(userMenu);
        map.put("menu", userMenu);
        
        ctx.setReqRes(req, res);
        
        return ctx;
    }
    
    private Boolean login(Response res, User o) {
        try {
            String token = Auth.generateAccessToken();
            this.accessTokenDao.insert(token, o.getId());

            res.cookie("access_token", token, 60 * 60 * 24 * 7);
        } catch(SQLException e) {
            return false;
        }
        
        return true;
    }
    
    private void logout(Response res, Context ctx) throws SQLException {
        this.accessTokenDao.delete(ctx.getAccessToken().getId());
        res.removeCookie("access_token");
    }
    
    private Route simple(Consumer<Context> fnc) {
        return (req, res) -> {
            Context ctx = getContext(req, res);
            
            if(fnc != null) {
                fnc.accept(ctx);
            }
            
            return null;
        };
    }
    
    private Route restricted(Consumer<Context> fnc) {
        return (req, res) -> {
            Context ctx = getContext(req, res);
            
            if(!ctx.isAdmin()) {
                halt(401);
            } else if(fnc != null) {
                fnc.accept(ctx);
            }
            
            return null;
        };
    }
    
    private TemplateViewRoute simpleView(String active, String layout) {
        return simpleView(active, layout, null);
    }
    
    private TemplateViewRoute simpleView(String active, String layout, Consumer<Context> fnc) { 
        return (req, res) -> {
            Context ctx = getContext(req, res);
            
            Menu menu = ctx.getMenu();
            HashMap map = ctx.getMap();
            
            if(menu.getItemExists(active)) {
                menu.setActive(active);
            } else {
                menu.removeActive();
                map.put("title", active);
            }

            if(fnc != null) {
                fnc.accept(ctx);
            }
            
            return new ModelAndView(map, layout);
        };
    }
    
    private TemplateViewRoute restrictedView(String active, String layout) {
        return restrictedView(active, layout, null);
    }
    
    private TemplateViewRoute restrictedView(String active, String layout, Consumer<Context> fnc) { 
        return (req, res) -> {
            Context ctx = getContext(req, res);
            
            if(!ctx.isAdmin()) {
                halt(401);
                
                return null;
            }
            
            Menu menu = ctx.getMenu();
            HashMap map = ctx.getMap();
            
            if(menu.getItemExists(active)) {
                menu.setActive(active);
            } else {
                menu.removeActive();
                map.put("title", active);
            }

            if(fnc != null) {
                fnc.accept(ctx);
            }
            
            return new ModelAndView(map, layout);
        };
    }
    
    public void start() {
        TemplateEngine engine = new MyTemplate();
        
        
        get("/", simpleView("home", "index", (Context ctx) -> {
            HashMap map = ctx.getMap();
                        
            List<Category> categories = this.categoryDao.findAll();
            
            map.put("categories", categories);
        }), engine);
        
        get("/kayttajat", simpleView("users", "kayttajat", (Context ctx) -> {
            List<User> users = this.userDao.findAll(0, 3);
            
            ctx.getMap().put("users", users);
        }), engine);
        
        get("/kayttajat/:sivu", simpleView("users", "kayttajat", (Context ctx) -> {
            Integer page = extractPage(ctx.getRequest().params(":sivu"));
            List<User> users = this.userDao.findAll((page - 1) * 3, 3);
            
            ctx.getMap().put("users", users);
        }), engine);
        
        get("/kirjaudu", simpleView("login", "kirjaudu", (Context ctx) -> {     
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            
            map.put("login-name", req.session().attribute("login-name"));
            req.session().attribute("login-name", null);
        }), engine);
        
        get("/rekisteroidy", simpleView("register", "rekisteroidy", (Context ctx) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            
            map.put("register-name", req.session().attribute("register-name"));
            req.session().attribute("register-name", null);
        }), engine);
                
        get("/alue/:id", simpleView("home", "index", (Context ctx) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            
            //List<Message> messages = this.messageDao.findAll();
            
            
            //return extractId(req.params(":id"));
        }), engine);
        
        get("/ulos", (req, res) -> {
            Context ctx = getContext(req, res);
            
            if(ctx.getAccessToken() != null) {
                logout(res, ctx);
                res.redirect("/");
            }
            
            return null; 
        });
                
        get("/uusi-alue", restrictedView("Uusi alue", "uusi-alue"), engine);
        
        post("/kirjaudu", simple((Context ctx) -> {
            Request req = ctx.getRequest();
            Response res = ctx.getResponse();
            
            if(req.queryParams("login-ok") != null) {
                String name = req.queryParams("login-name").trim();
                String pw = req.queryParams("login-pw");
                
                User o = (User)this.userDao.findOneBy("name", name);
                if(o == null || !Auth.passwordMatches(o, pw)) {
                    req.session().attribute("login-name", name);
                    req.session().attribute("error", "Kirjautuminen epäonnistui: virheellinen käyttäjätunnus tai salasana");
                    res.redirect("/kirjaudu");
                } else {
                    if(login(res, o)) {
                        req.session().attribute("success", "Kirjautuminen onnistui");
                    } else {
                        req.session().attribute("error", "Kirjautuminen epäonnistui: tuntematon syy");
                    }
                    
                    res.redirect("/");
                }
            }
        }));
        
        post("/uusi-alue", restricted((Context ctx) -> {
            Request req = ctx.getRequest();
            
            if(req.queryParams("category-ok") != null) {
                String name = req.queryParams("category-name").trim();
                
                Validator nameValidator = new Validator();
                nameValidator.addRule(new MinLengthRule(3, "nimen pitää olla yli 3 merkkiä"));
                
                String error = null;
                
                Category o = (Category)this.categoryDao.findOneBy("name", name);
                
                if(o != null) {
                    error = "saman nimien alue on jo olemassa";
                } else if(!nameValidator.validate(name)) {
                    error = nameValidator.getReason();
                }
                
                if(error == null) {
                    
                }
            }
        }));
        
        post("/rekisteroidy", simple((Context ctx) -> {
            Request req = ctx.getRequest();
            Response res = ctx.getResponse();
            
            if(req.queryParams("register-ok") != null) {
                String name = req.queryParams("register-name").trim();
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
                
                User o = (User)this.userDao.findOneBy("name", name);
                
                if(o != null) {
                    error = "nimi on jo käytössä";
                } else if(!nameValidator.validate(name)) {
                    error = nameValidator.getReason();
                } else if(!pwValidator.validate(pw)) {
                    error = pwValidator.getReason();
                }
                
                if(error == null) {
                    try {
                        userDao.insert(name, pw, false);
                    } catch(Exception e) {
                        error = "tuntematon syy";
                    }
                }
                
                if(error != null) {
                    req.session().attribute("error", "Rekisteröityminen epäonnistui: " + error);
                    res.redirect("/rekisteroidy");
                } else {
                    o = (User)this.userDao.findOneBy("name", name);
                    login(res, o);

                    req.session().attribute("success", "Rekisteröityminen onnistui");
                    res.redirect("/");
                }
            }
        }));
    }
}
