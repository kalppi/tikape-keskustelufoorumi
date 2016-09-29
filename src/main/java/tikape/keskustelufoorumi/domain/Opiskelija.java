package tikape.keskustelufoorumi.domain;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Base64.Decoder;
import javax.crypto.SecretKeyFactory;

public class Opiskelija {
    private Integer id;
    private String nimi;
    private String pwHash;

    public Opiskelija(Integer id, String nimi, String pwHash) {
        this.id = id;
        this.nimi = nimi;
        this.pwHash = pwHash;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public String getPwHash() {
        return this.pwHash;
    }
    
    @Override
    public String toString() {
        return "[" + this.id + "/" + this.nimi + "]";
    }
}