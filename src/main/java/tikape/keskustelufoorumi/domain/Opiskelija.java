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
    
    public Boolean passwordMatches(String pw) {
        try {
            String salt = this.pwHash.substring(0, 44);
            Decoder decoder = Base64.getDecoder();
            byte[] saltBytes = decoder.decode(salt);
            
            String hash = Opiskelija.hashPassword(pw, saltBytes);
            return hash.equals(this.pwHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "[" + this.id + "/" + this.nimi + "]";
    }
    
    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[32];
        sr.nextBytes(salt);
        
        return salt;
    }
    
    public static String hashPassword(String pw) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Opiskelija.hashPassword(pw, Opiskelija.generateSalt());
    }
    
    public static String hashPassword(String pw, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        
        java.security.spec.KeySpec spec = new javax.crypto.spec.PBEKeySpec(pw.toCharArray(), salt, iterations, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(salt) + enc.encodeToString(hash);
    }
}
