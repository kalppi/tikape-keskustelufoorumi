package tikape.keskustelufoorumi.domain;

import java.util.List;

/**
 *
 * @author jarnoluu
 */
public class Thread {
    private Integer id;
    private Integer categoryId;
    private String title;
    private Integer messageCount;
    private Message firstMessage;
    private Message latestMessage;

    public Thread(Integer id, Integer categoryId, String title, Integer messageCount, Message firstMessage, Message latestMessage) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.messageCount = messageCount;
        this.firstMessage = firstMessage;
        this.latestMessage = latestMessage;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCategory_id() {
        return categoryId;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public Message getFirstMessage() {
        return this.firstMessage;
    }

    public Message getLatestMessage() {
        return this.latestMessage;
    }
    
    public Integer getMessageCount() {
        return this.messageCount;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCategory_id(Integer category_id) {
        this.categoryId = category_id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLatestMessage(Message message) {
        this.latestMessage = message;
    }
}
