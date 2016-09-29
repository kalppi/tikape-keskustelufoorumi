package tikape.keskustelufoorumi.domain;

/**
 *
 * @author jarno
 */
public class AccessToken {
    private Integer id;
    private String token;
    private Integer userId;
    
    public AccessToken(Integer id, String token, Integer userId) {
        this.id = id;
        this.token = token;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Integer getUserId() {
        return userId;
    }
}
