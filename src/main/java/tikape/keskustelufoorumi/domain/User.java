package tikape.keskustelufoorumi.domain;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Decoder;
import javax.crypto.SecretKeyFactory;

public class User {
    private Integer id;
    private String name;
    private String pwHash;
    private Boolean admin;
    private LocalDateTime registered;

    public User(Integer id, String name, String pwHash, Boolean admin, LocalDateTime registered) {
        this.id = id;
        this.name = name;
        this.pwHash = pwHash;
        this.admin = admin;
        this.registered = registered;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getPwHash() {
        return this.pwHash;
    }
    
    public Boolean getAdmin() {
        return this.admin;
    }
    
    public LocalDateTime getRegistered() {
        return this.registered;
    }
    
    @Override
    public String toString() {
        return "[" + this.id + "/" + this.name + "]";
    }
}