package tikape.runko;

import java.net.URI;
import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Viesti;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 4567;
        String jdbcOsoite = "jdbc:sqlite:keskustelufoorumi.db";
        String username = "";
        String password = "";
        
        boolean production = false;
        
        //herokua varten
        if(System.getenv("PORT") != null) {
            production = true;
            port = Integer.parseInt(System.getenv("PORT"));
        }
        if (System.getenv("DATABASE_URL") != null) {            
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            username = dbUri.getUserInfo().split(":")[0];
            password = dbUri.getUserInfo().split(":")[1];
            jdbcOsoite = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        } 
        
        port(port);
        
        Database database = new Database(jdbcOsoite, username, password);
        database.init();

        OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelijat", opiskelijaDao.findAll());

            return new ModelAndView(map, "opiskelijat");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", opiskelijaDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "opiskelija");
        }, new ThymeleafTemplateEngine());

        if(!production) {
            Thread.sleep(1000);

            TextUI tui = new TextUI(database);
            tui.start();
        }
    }
}
