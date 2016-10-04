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
    private Message latestMessage;

    public Thread(Integer id, Integer categoryId, String title, Message latestMessage) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
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

    public Message getLatestMessage() {
        return latestMessage;
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
