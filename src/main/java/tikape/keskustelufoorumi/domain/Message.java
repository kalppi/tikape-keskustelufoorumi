/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private Integer id;
    private User user;
    private Integer threadId;
    private LocalDateTime sent;
    private String text;

    public Message(Integer id, User user, Integer threadId, LocalDateTime sent, String text) {
        this.id = id;
        this.user = user;
        this.threadId = threadId;
        this.sent = sent;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }
    
    public void setUser(User o) {
        this.user = o;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public String getText() {
        return this.text;
    }
    
    public LocalDateTime getSent() {
        return this.sent;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return "[#" + this.id + " @" + this.sent.format(formatter) + " " + this.user + ": \"" + this.text + "\"]";
    }
}
