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
    
    public void start() throws SQLException {        
        this.reader = new Scanner(System.in);
        System.out.println("Tekstikäyttöliittymä");
        System.out.println("Komennot:");
        System.out.println("  hae-viesti [id, id2, id3, ...]");
        while(true) {
            System.out.println(">> ");
            String line = this.reader.nextLine();
            
            ViestiDao vd = new ViestiDao(this.database);
            
            try {
                String[] parts = line.split(" ");
                switch(parts[0]) {
                    case "hae-viesti":
                        if(parts[1].contains(",")) {
                            String[] idParts = parts[1].split(",");
                            List<Integer> ids = new ArrayList();
                            for(String idPart : idParts) {
                                ids.add(Integer.parseInt(idPart.trim()));
                            }
                            
                            List<Viesti> vs = vd.findAllIn(ids);
                            for(Viesti v : vs) {
                                System.out.println(v);
                            }
                        } else {
                            Integer id = Integer.parseInt(parts[1]);

                            Viesti v = vd.findOne(id);

                            System.out.println(v);
                        }

                        break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
