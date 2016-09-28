/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.domain;

/**
 *
 * @author jarno
 */
public class AccessToken {
    private Integer id;
    private String token;
    private Integer opiskelijaId;
    
    public AccessToken(Integer id, String token, Integer opiskelijaId) {
        this.id = id;
        this.token = token;
        this.opiskelijaId = opiskelijaId;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Integer getOpiskelijaId() {
        return opiskelijaId;
    }
}
