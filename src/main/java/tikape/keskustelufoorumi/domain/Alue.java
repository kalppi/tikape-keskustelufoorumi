/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Alue {
    private Integer id;
    private String nimi;
    
    public Alue(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public String getNimi() {
        return this.nimi;
    }
    
    public Integer getPostCount() {
        return (int)(Math.random() * 10);
    }
    
    public String getLatestPostTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return new Viesti(1, null, 0, LocalDateTime.now(), "asdasd").getAika().format(formatter);
    }
}
