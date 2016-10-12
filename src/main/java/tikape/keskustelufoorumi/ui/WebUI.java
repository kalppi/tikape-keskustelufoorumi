package tikape.keskustelufoorumi.ui;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
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
import tikape.keskustelufoorumi.database.ThreadDao;
import tikape.keskustelufoorumi.domain.Message;

public class WebUI implements UI {
    private final Integer THREADS_IN_PAGE = 5;
    
    private Database database;
    
    private UserDao userDao;
    private CategoryDao categoryDao;
    private ThreadDao threadDao;
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
        this.threadDao = new ThreadDao(this.database);
        this.messageDao = new MessageDao(this.database);
        
        int port = 4567;
        
        //herokua varten
        if(System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        }
        
        port(port);
        staticFileLocation("/public");
        
        this.menu = new Menu();
        this.menu.addItem("home", "Keskustelu", "/");
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
        Session ses = req.session();
        
        String path = req.pathInfo();
        
        if(!path.equals(ses.attribute("currentPage"))) {
            ses.attribute("lastPage", ses.attribute("currentPage"));
            ses.attribute("currentPage", path);
        }
        
        ctx.setLastPage(ses.attribute("lastPage"));        
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
        
        /*
        
        Siirretään mahdollisten ilmoitusten arvot sessiomuuttujista näkymän mappiin
        
        */
        
        map.put("success", ses.attribute("success"));
        map.put("error", ses.attribute("error"));
        map.put("info", ses.attribute("info"));
        map.put("warning", ses.attribute("warning"));
        
        ses.attribute("success", null);
        ses.attribute("error", null);
        ses.attribute("info", null);
        ses.attribute("warning", null);
        
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
            Session ses = req.session();
            
            map.put("lastPage", ctx.getLastPage());
            map.put("login-name", req.session().attribute("login-name"));
            req.session().attribute("login-name", null);
        }), engine);
        
        get("/rekisteroidy", simpleView("register", "rekisteroidy", (Context ctx) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            
            map.put("lastPage", ctx.getLastPage());
            map.put("register-name", req.session().attribute("register-name"));
            req.session().attribute("register-name", null);
        }), engine);
                        
        BiConsumer<Context, Integer> threadsFunc = (Context ctx, Integer page) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            Session ses = req.session();
            
            Integer id = extractId(req.params(":id"));
                    
            Category cat = this.categoryDao.findOne(id);
            List<tikape.keskustelufoorumi.domain.Thread> threads = this.threadDao.findAllBy("category_id", id, (page - 1) * this.THREADS_IN_PAGE, this.THREADS_IN_PAGE);
            
            Integer pages = (int)Math.ceil(this.threadDao.countBy("category_id", cat.getId()) / (double)this.THREADS_IN_PAGE);
            
            map.put("thread-title", ses.attribute("thread-title"));
            map.put("thread-text", ses.attribute("thread-text"));
            
            ses.attribute("thread-title", null);
            ses.attribute("thread-text", null);
            
            map.put("title", "Alue: " + cat.getName());
            map.put("category", cat);
            map.put("threads", threads);
            map.put("pages", pages);
            map.put("currentPage", page);
        };
        
        get("/alue/:id", simpleView("home", "alue", (Context ctx) -> {
            threadsFunc.accept(ctx, 1);
        }), engine);
        
        get("/alue/:id/sivu/:page", simpleView("home", "alue", (Context ctx) -> {
            Request req = ctx.getRequest();
            
            Integer page = 1;
            try {
                page = Integer.parseInt(req.params(":page"));
            } catch(Exception e) {
                
            }
            
            threadsFunc.accept(ctx, page);
        }), engine);
                
        post("/alue/:id", restricted((Context ctx) -> {
            Request req = ctx.getRequest();
            Response res = ctx.getResponse();
            
            if(req.queryParams("thread-ok") != null) {
                String title = req.queryParams("thread-title").trim();
                String text = req.queryParams("thread-text").trim();
                Session ses = req.session();
                
                Integer categoryId;
                try {
                    categoryId = Integer.parseInt(req.params(":id"));
                } catch(Exception e) {
                    return;
                }
                
                Validator titleValidator = new Validator();
                titleValidator.addRule(new MinLengthRule(3, "otsikon pitää olla vähintään 3 merkkiä"));
                titleValidator.addRule(new MaxLengthRule(255, "otsikko saa olla enintään 255 merkkiä"));
                
                Validator messageValidator = new Validator();
                messageValidator.addRule(new MinLengthRule(3, "viestin pitää olla vähintään 3 merkkiä"));
                messageValidator.addRule(new MaxLengthRule(2000, "viesti saa olla enintään 2000 merkkiä"));
                
                String error = null;
                                
                if(!titleValidator.validate(title)) {
                    error = titleValidator.getReason();
                } else if(!messageValidator.validate(text)) {
                    error = messageValidator.getReason();
                }
                
                ses.attribute("thread-title", title);
                ses.attribute("thread-text", text);
                
                if(error == null) {
                    try {
                        this.threadDao.insert(categoryId, title, ctx.getLoggedInUser().getId(), text);
                        
                        ses.attribute("thread-title", null);
                        ses.attribute("thread-text", null);
                    } catch(Exception e) {
                        ses.attribute("error", "Uuden ketjun luominen epäonnistui: tuntematon virhe");
                    }
                } else {
                    ses.attribute("error", "Uuden ketjun luominen epäonnistui: " + error);
                }
                
                res.redirect(req.url());
            }
        }));
        
        post("/ketju/:id", restricted((Context ctx) -> {
            Request req = ctx.getRequest();
            Response res = ctx.getResponse();
            
            if(req.queryParams("message-ok") != null) {
                String text = req.queryParams("message-text").trim();
                Session ses = req.session();
                
                Integer threadId;
                try {
                    threadId = Integer.parseInt(req.params(":id"));
                } catch(Exception e) {
                    return;
                }
                
                Validator messageValidator = new Validator();
                messageValidator.addRule(new MinLengthRule(3, "viestin pitää olla vähintään 3 merkkiä"));
                messageValidator.addRule(new MaxLengthRule(2000, "viesti saa olla enintään 2000 merkkiä"));
                
                String error = null;
                                
                if(!messageValidator.validate(text)) {
                    error = messageValidator.getReason();
                }
                
                ses.attribute("message-text", text);
                
                if(error == null) {
                    try {
                        this.messageDao.insert(threadId, ctx.getLoggedInUser().getId(), text);
                        
                        ses.attribute("message-text", null);
                    } catch(Exception e) {
                        ses.attribute("error", "Viestin lähettäminen epäonnistui: tuntematon virhe");
                    }
                } else {
                    ses.attribute("error", "Viestin lähettäminen epäonnistui: " + error);
                }
                
                res.redirect(req.url());
            }
        }));
        
        get("/ketju/:id", simpleView("home", "ketju", (Context ctx) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            Session ses = req.session();
            
            Integer id = extractId(req.params(":id"));
            
            tikape.keskustelufoorumi.domain.Thread thread = this.threadDao.findOne(id);
            Category category = this.categoryDao.findOne(thread.getCategory_id());
            List<Message> messages = this.messageDao.findAllBy("thread_id", id);
            
            map.put("message-text", ses.attribute("message-text"));
            ses.attribute("message-text", null);
            
            map.put("title", "Ketju: " + thread.getTitle());
            map.put("category", category);
            map.put("thread", thread);
            map.put("messages", messages);
        }), engine);
        
        get("/ulos", (req, res) -> {
            Context ctx = getContext(req, res);
            Session ses = req.session();
            
            if(ctx.getAccessToken() != null) {
                logout(res, ctx);
                res.redirect(ctx.getLastPage());
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
                String url = req.queryParams("url");
                
                User o = (User)this.userDao.findOneBy("name", name);
                if(o == null || !Auth.passwordMatches(o, pw)) {
                    req.session().attribute("login-name", name);
                    req.session().attribute("error", "Kirjautuminen epäonnistui: virheellinen käyttäjätunnus tai salasana");
                    res.redirect("/kirjaudu");
                } else {
                    if(login(res, o)) {
                        req.session().attribute("success", "Kirjautuminen onnistui");
                        res.redirect(url);
                    } else {
                        req.session().attribute("error", "Kirjautuminen epäonnistui: tuntematon syy");
                        res.redirect("/kirjaudu");
                    }
                }
            }
        }));
        
        post("/uusi-alue", restricted((Context ctx) -> {
            Request req = ctx.getRequest();
            
            if(req.queryParams("category-ok") != null) {
                String name = req.queryParams("category-name").trim();
                
                Validator nameValidator = new Validator();
                nameValidator.addRule(new MinLengthRule(3, "nimen pitää olla vähintään 3 merkkiä"));
                
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
                String url = req.queryParams("url");
                
                req.session().attribute("register-name", name);
                
                Validator nameValidator = new Validator();
                nameValidator.addRule(new MinLengthRule(3, "nimen pitää olla vähintään 3 merkkiä"));
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
                    res.redirect(url);
                }
            }
        }));
    }
}
