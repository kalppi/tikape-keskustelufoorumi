package tikape.keskustelufoorumi.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import tikape.keskustelufoorumi.ui.UI;
import tikape.keskustelufoorumi.database.Database;
import tikape.keskustelufoorumi.database.MessageDao;
import tikape.keskustelufoorumi.domain.Message;
import tikape.keskustelufoorumi.database.IDao;

public class TextUI implements UI {
    private Database database;
    private Scanner reader = null;
    private IDao viestiDao = null;
    
    public TextUI(Database database) {
        this.database = database;
    }
    
    public void init() throws SQLException {
        this.viestiDao = new MessageDao(this.database);
    }
    
    private List<Integer> getIds(String str) {
        String[] idParts = str.split(",");
        List<Integer> ids = new ArrayList();
        
        for(String idPart : idParts) {
            ids.add(Integer.parseInt(idPart.trim()));
        }
        
        return ids;
    }
    
    public void start() {    
        this.reader = new Scanner(System.in);
                
        System.out.println("Tekstikäyttöliittymä");
        System.out.println("Komennot:");
        System.out.println("  hae-viesti [id, id2, id3, ...]");
        System.out.println("  hae-käyttäjä [id]");
        System.out.println("  hae-ketju [id]");
        
        while(true) {
            System.out.println(">> ");
            String line = "";
            
            if(this.reader.hasNextLine()) {
                line = this.reader.nextLine();
            }
            
            try {
                String[] parts = line.split(" ");
                switch(parts[0]) {
                    case "hae-viesti":
                        List<Integer> ids = getIds(parts[1]);
                        List<Message> vs = this.viestiDao.findAllIn(ids);
                        
                        for(Message v : vs) {
                            System.out.println(v);
                        }

                        break;
                    case "hae-käyttäjä":
                        
                        break;
                    case "hae-ketju":
                        
                        break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
