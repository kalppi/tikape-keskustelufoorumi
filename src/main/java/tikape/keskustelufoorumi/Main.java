package tikape.keskustelufoorumi;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import tikape.keskustelufoorumi.database.Database;

public class Main {
    public static void main(String[] args) throws Exception {
        String jdbcOsoite = "jdbc:sqlite:keskustelufoorumi.db";
        String username = "";
        String password = "";
        
        boolean production = false;
        
        if (System.getenv("DATABASE_URL") != null) {            
            production = true;
            
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            username = dbUri.getUserInfo().split(":")[0];
            password = dbUri.getUserInfo().split(":")[1];
            jdbcOsoite = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        } 
        
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
