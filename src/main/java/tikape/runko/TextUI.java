/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import tikape.runko.database.Database;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Viesti;

/**
 *
 * @author jarno
 */
public class TextUI {
    private Database database;
    private Scanner reader = null;
    
    public TextUI(Database database) {
        this.database = database;
    }
    
    private List<Integer> getIds(String str) {
        String[] idParts = str.split(",");
        List<Integer> ids = new ArrayList();
        
        for(String idPart : idParts) {
            ids.add(Integer.parseInt(idPart.trim()));
        }
        
        return ids;
    }
    
    public void start() throws SQLException {        
        this.reader = new Scanner(System.in);
        System.out.println("Tekstikäyttöliittymä");
        System.out.println("Komennot:");
        System.out.println("  hae-viesti [id, id2, id3, ...]");
        System.out.println("  hae-käyttäjä [id]");
        System.out.println("  hae-ketju [id]");
        while(true) {
            System.out.println(">> ");
            String line = this.reader.nextLine();
            
            ViestiDao vd = new ViestiDao(this.database);
            
            try {
                String[] parts = line.split(" ");
                switch(parts[0]) {
                    case "hae-viesti":
                        List<Integer> ids = getIds(parts[1]);
                        List<Viesti> vs = vd.findAllIn(ids);
                        
                        for(Viesti v : vs) {
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
