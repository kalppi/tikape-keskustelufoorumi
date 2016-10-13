/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Consumer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
import static spark.Spark.halt;
import spark.TemplateViewRoute;
import tikape.keskustelufoorumi.domain.AccessToken;
import tikape.keskustelufoorumi.database.AccessTokenDao;
import tikape.keskustelufoorumi.database.Database;
import tikape.keskustelufoorumi.database.UserDao;
import tikape.keskustelufoorumi.domain.User;
import tikape.keskustelufoorumi.ui.Menu;

/**
 *
 * @author jarno
 */
public class ViewManager {
    private UserDao userDao;
    private AccessTokenDao accessTokenDao;
    private Menu menu;
    
    public ViewManager(Database database, Menu menu) throws SQLException {
        this.accessTokenDao = new AccessTokenDao(database);
        this.userDao = new UserDao(database);
        
        this.menu = menu;
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
    
    public Route simple(Consumer<Context> fnc) {
        return (req, res) -> {
            Context ctx = getContext(req, res);
            
            if(fnc != null) {
                fnc.accept(ctx);
            }
            
            return null;
        };
    }
    
    public Route adminRequired(Consumer<Context> fnc) {
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
    
    public Route loginRequired(Consumer<Context> fnc) {
        return (req, res) -> {
            Context ctx = getContext(req, res);
            
            if(ctx.getLoggedInUser() == null) {
                halt(401);
            } else if(fnc != null) {
                fnc.accept(ctx);
            }
            
            return null;
        };
    }
    
    public TemplateViewRoute simpleView(String active, String layout) {
        return simpleView(active, layout, null);
    }
    
    public TemplateViewRoute simpleView(String active, String layout, Consumer<Context> fnc) { 
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
    
    public TemplateViewRoute adminRequiredView(String active, String layout) {
        return adminRequiredView(active, layout, null);
    }
    
    public TemplateViewRoute adminRequiredView(String active, String layout, Consumer<Context> fnc) { 
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
    
    public TemplateViewRoute loginRequiredView(String active, String layout) {
        return loginRequiredView(active, layout, null);
    }
    
    public TemplateViewRoute loginRequiredView(String active, String layout, Consumer<Context> fnc) { 
        return (req, res) -> {
            Context ctx = getContext(req, res);
            
            if(ctx.getLoggedInUser() == null) {
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
}
