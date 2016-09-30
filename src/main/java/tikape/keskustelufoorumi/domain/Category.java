package tikape.keskustelufoorumi.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Category {
    private Integer id;
    private String name;
    private Integer messageCount;
    private Message latestMessage;
    
    public Category(Integer id, String name, Integer messageCount, Message latestMessage) {
        this.id = id;
        this.name = name;
        this.messageCount = messageCount;
        this.latestMessage = latestMessage;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Integer getPostCount() {
        return this.messageCount;
    }
    
    public Message getLatestMessage() {
        return this.latestMessage;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }
}
