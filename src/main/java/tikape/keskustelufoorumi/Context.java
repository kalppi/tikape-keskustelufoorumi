/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi;

import java.util.HashMap;
import tikape.keskustelufoorumi.domain.Opiskelija;

/**
 *
 * @author jarno
 */
public class Context {
    private Opiskelija loggedInUser;
    private HashMap map;    
    
    public Context() {
        
    }

    public Opiskelija getLoggedInUser() {
        return loggedInUser;
    }

    public HashMap getMap() {
        return map;
    }

    public void setLoggedInUser(Opiskelija loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void setMap(HashMap map) {
        this.map = map;
    }
}
