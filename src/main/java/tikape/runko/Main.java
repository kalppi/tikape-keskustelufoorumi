package tikape.runko;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        
        List<UI> uis = new ArrayList();
        
        uis.add(new WebUI(database));

        if(!production) {
            uis.add(new TextUI(database));
        }
        
        try {
            for(UI ui : uis) {
                ui.init();
                ui.start();
            }
        } catch(Exception e) {
            System.out.println("Käyttöliittymien käynnistäminen ei onnistunut");
            e.printStackTrace();
        }
    }
}
