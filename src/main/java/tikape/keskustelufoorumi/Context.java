package tikape.keskustelufoorumi;

import java.util.HashMap;
import spark.Request;
import spark.Response;
import tikape.keskustelufoorumi.domain.AccessToken;
import tikape.keskustelufoorumi.domain.Opiskelija;
import tikape.keskustelufoorumi.ui.Menu;

/**
 *
 * @author jarno
 */
public class Context {
    private Opiskelija loggedInUser;
    private AccessToken accessToken;
    private Menu menu;
    private HashMap map;
    private Request req;
    private Response res;
    
    public Context() {
        
    }

    public Opiskelija getLoggedInUser() {
        return loggedInUser;
    }
    
    public AccessToken getAccessToken() {
        return this.accessToken;
    }
    
    public Menu getMenu() {
        return this.menu;
    }

    public HashMap getMap() {
        return map;
    }
    
    public Request getRequest() {
        return this.req;
    }
    
    public Response getResponse() {
        return this.res;
    }

    public void setLoggedInUser(Opiskelija loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    
    public void setAccessToken(AccessToken token) {
        this.accessToken = token;
    }
    
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setMap(HashMap map) {
        this.map = map;
    }
    
    public void setReqRes(Request req, Response res) {
        this.req = req;
        this.res = res;
    }
}
