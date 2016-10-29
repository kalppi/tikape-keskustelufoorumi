package tikape.keskustelufoorumi.ui;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import spark.Request;
import spark.Response;
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
import tikape.keskustelufoorumi.validator.EqualsRule;
import tikape.keskustelufoorumi.database.CategoryDao;
import tikape.keskustelufoorumi.database.MessageDao;
import tikape.keskustelufoorumi.database.ThreadDao;
import tikape.keskustelufoorumi.domain.Message;
import static spark.Spark.get;
import static spark.Spark.post;
import tikape.keskustelufoorumi.ViewManager;

public class WebUI implements UI {
    private final Integer THREADS_IN_PAGE = 5;
    private final Integer MESSAGES_IN_PAGE = 5;
    
    private Database database;
    
    private UserDao userDao;
    private CategoryDao categoryDao;
    private ThreadDao threadDao;
    private MessageDao messageDao;
    
    private ViewManager viewManager;
    
    private AccessTokenDao accessTokenDao;
    
    Validator messageValidator;
    Validator titleValidator;
    Validator categoryNameValidator;
    Validator userNameValidator;
        
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
        
        Menu menu = new Menu();
        menu.addItem("home", "Keskustelu", "/");
        menu.addItem("users", "Käyttäjät", "/kayttajat");
        menu.addItem("login", "Kirjaudu", "/kirjaudu", (Context ctx) -> {
            return ctx.getLoggedInUser() == null;
        });
        menu.addItem("register", "Rekisteröidy", "/rekisteroidy", (Context ctx) -> {
            return ctx.getLoggedInUser() == null;
        });
        menu.addItem("logout", "Kirjaudu ulos", "/ulos", (Context ctx) -> {
            return ctx.getLoggedInUser() != null;
        });
        
        this.viewManager = new ViewManager(this.database, menu);
        
        this.messageValidator = new Validator();
        this.messageValidator.addRule(new MinLengthRule(3, "viestin pitää olla vähintään 3 merkkiä"));
        this.messageValidator.addRule(new MaxLengthRule(2000, "viesti saa olla enintään 2000 merkkiä"));
        
        this.titleValidator = new Validator();
        this.titleValidator.addRule(new MinLengthRule(3, "otsikon pitää olla vähintään 3 merkkiä"));
        this.titleValidator.addRule(new MaxLengthRule(255, "otsikko saa olla enintään 255 merkkiä"));
        
        this.categoryNameValidator = new Validator();
        this.categoryNameValidator.addRule(new MinLengthRule(3, "nimen pitää olla vähintään 3 merkkiä"));
        
        this.userNameValidator = new Validator();
        this.userNameValidator.addRule(new MinLengthRule(3, "nimen pitää olla vähintään 3 merkkiä"));
        this.userNameValidator.addRule(new MaxLengthRule(20, "nimi ei saa olla yli 20 merkkiä pitkä"));
        this.userNameValidator.addRule(new PatternRule("^[a-zA-Z0-9]+$", "nimi saa sisältää vain kirjaimia ja numeroita"));
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
    
    private void logout(Response res, Context ctx) {
        this.accessTokenDao.delete(ctx.getAccessToken().getId());
        res.removeCookie("access_token");
    }

    public void start() {
        TemplateEngine engine = new MyTemplate();
        
        get("/", this.viewManager.simpleView("home", "index", (Context ctx) -> {
            HashMap map = ctx.getMap();
                        
            List<Category> categories = this.categoryDao.findAll();
            
            map.put("categories", categories);
        }), engine);
        
        get("/kayttajat", this.viewManager.simpleView("users", "kayttajat", (Context ctx) -> {
            List<User> users = this.userDao.findAll();
            
            ctx.getMap().put("users", users);
        }), engine);
        
        get("/kayttaja/:id", this.viewManager.simpleView("users", "kayttaja", (Context ctx) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            
            Integer id;
            
            try {
                id = Integer.parseInt(req.params(":id"));
            } catch(Exception e) {
                return;
            }
            
            User user = this.userDao.findOne(id);
            
            if(user == null) {
                return;
            }
            
            map.put("userProfile", user);
        }), engine);
                
        get("/kirjaudu", this.viewManager.simpleView("login", "kirjaudu", (Context ctx) -> {     
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            
            map.put("lastPage", ctx.getLastPage());
            map.put("login-name", req.session().attribute("login-name"));
            req.session().attribute("login-name", null);
        }), engine);
        
        get("/rekisteroidy", this.viewManager.simpleView("register", "rekisteroidy", (Context ctx) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            
            map.put("lastPage", ctx.getLastPage());
            map.put("register-name", req.session().attribute("register-name"));
            req.session().attribute("register-name", null);
        }), engine);
                        
        BiConsumer<Context, Integer> categoryFunc = (Context ctx, Integer page) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            Session ses = req.session();
            
            Integer id = extractId(req.params(":id"));
                    
            Category cat = this.categoryDao.findOne(id);
            
            if(cat == null) {
                return;
            }
            
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
        
        get("/alue/:id", this.viewManager.simpleView("home", "alue", (Context ctx) -> {
            categoryFunc.accept(ctx, 1);
        }), engine);
        
        get("/alue/:id/sivu/:page", this.viewManager.simpleView("home", "alue", (Context ctx) -> {
            Request req = ctx.getRequest();
            
            Integer page = 1;
            try {
                page = Integer.parseInt(req.params(":page"));
            } catch(Exception e) {
                
            }
            
            categoryFunc.accept(ctx, page);
        }), engine);
                
        post("/alue/:id", this.viewManager.loginRequired((Context ctx) -> {
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
                                
                String error = null;
                                
                if(!this.titleValidator.validate(title)) {
                    error = this.titleValidator.getReason();
                } else if(!this.messageValidator.validate(text)) {
                    error = this.messageValidator.getReason();
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
        
        post("/ketju/:id", this.viewManager.loginRequired((Context ctx) -> {
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
                                
                String error = null;
                                
                if(!this.messageValidator.validate(text)) {
                    error = this.messageValidator.getReason();
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
        
        BiConsumer<Context, Integer> threadFunc = (Context ctx, Integer page) -> {
            Request req = ctx.getRequest();
            HashMap map = ctx.getMap();
            Session ses = req.session();
            
            Integer id = extractId(req.params(":id"));
            
            tikape.keskustelufoorumi.domain.Thread thread = this.threadDao.findOne(id);
            
            if(thread == null) {
                return;
            }
            
            Category category = this.categoryDao.findOne(thread.getCategory_id());
            List<Message> messages = this.messageDao.findAllBy("thread_id", id, (page - 1) * this.THREADS_IN_PAGE, this.THREADS_IN_PAGE);
            
            Integer pages = (int)Math.ceil(this.messageDao.countBy("thread_id", thread.getId()) / (double)this.MESSAGES_IN_PAGE);
            
            map.put("message-text", ses.attribute("message-text"));
            ses.attribute("message-text", null);
            
            map.put("title", "Ketju: " + thread.getTitle());
            map.put("category", category);
            map.put("thread", thread);
            map.put("messages", messages);
            map.put("pages", pages);
            map.put("currentPage", page);
        };
        
        get("/ketju/:id", this.viewManager.simpleView("home", "ketju", (Context ctx) -> {
            threadFunc.accept(ctx, 1);
        }), engine);
        
        get("/ketju/:id/sivu/:page", this.viewManager.simpleView("home", "ketju", (Context ctx) -> {
            Request req = ctx.getRequest();
            
            Integer page = 1;
            try {
                page = Integer.parseInt(req.params(":page"));
            } catch(Exception e) {
                
            }
            
            threadFunc.accept(ctx, page);
        }), engine);
        
        get("/ulos", this.viewManager.loginRequired((Context ctx) -> {
            Response res = ctx.getResponse();
            
            if(ctx.getAccessToken() != null) {
                logout(res, ctx);
                res.redirect(ctx.getLastPage());
            }
        }));
        
        post("/kirjaudu", this.viewManager.simple((Context ctx) -> {
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
        
        get("/uusi-alue", this.viewManager.adminRequiredView("Uusi alue", "uusi-alue"), engine);
        
        post("/uusi-alue", this.viewManager.adminRequired((Context ctx) -> {
            Request req = ctx.getRequest();
            
            if(req.queryParams("category-ok") != null) {
                String name = req.queryParams("category-name").trim();
                                
                String error = null;
                
                Category o = (Category)this.categoryDao.findOneBy("name", name);
                
                if(o != null) {
                    error = "saman nimien alue on jo olemassa";
                } else if(!this.categoryNameValidator.validate(name)) {
                    error = this.categoryNameValidator.getReason();
                }
                
                if(error == null) {
                    try {
                        this.categoryDao.insert(name);
                    } catch(Exception e) {
                        error = "tuntematon virhe";
                    }
                }
                
                if(error != null) {
                    req.session().attribute("error", "Alueen luominen epäonnistui: " + error);
                } else {
                    req.session().attribute("success", "Alueen luominen onnistui");
                }
                
                ctx.getResponse().redirect("/");
            }
        }));
        
        post("/rekisteroidy", this.viewManager.simple((Context ctx) -> {
            Request req = ctx.getRequest();
            Response res = ctx.getResponse();
            
            if(req.queryParams("register-ok") != null) {
                String name = req.queryParams("register-name").trim();
                String pw = req.queryParams("register-pw");
                String pw2 = req.queryParams("register-pw2");
                String url = req.queryParams("url");
                
                req.session().attribute("register-name", name);
                
                Validator pwValidator = new Validator();
                pwValidator.addRule(new MinLengthRule(8, "salasanan pitää olla vähintään 8 merkkiä pitkä"));
                pwValidator.addRule(new EqualsRule(pw2, "salasanat eivät ole samoja"));
                
                String error = null;
                
                User o = (User)this.userDao.findOneBy("name", name);
                
                if(o != null) {
                    error = "nimi on jo käytössä";
                } else if(!this.userNameValidator.validate(name)) {
                    error = this.userNameValidator.getReason();
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