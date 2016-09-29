/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Category {
    private Integer id;
    private String name;
    
    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Integer getPostCount() {
        return (int)(Math.random() * 10);
    }
    
    public String getLatestPostTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return new Message(1, null, 0, LocalDateTime.now(), "asdasd").getSent().format(formatter);
    }
}
