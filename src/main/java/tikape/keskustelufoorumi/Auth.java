/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import tikape.keskustelufoorumi.domain.Opiskelija;

/**
 *
 * @author jarno
 */
public class Auth {
    public static String generateAcccessToken() {
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[256];
        sr.nextBytes(bytes);
        
        Base64.Encoder enc = Base64.getEncoder();
        
        return enc.encodeToString(bytes);
    }
    
    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[32];
        sr.nextBytes(salt);
        
        return salt;
    }
    
    public static String hashPassword(String pw) throws Exception {
        return Auth.hashPassword(pw, Auth.generateSalt());
    }
    
    public static String hashPassword(String pw, byte[] salt) throws Exception {
        try {
            int iterations = 1000;

            java.security.spec.KeySpec spec = new javax.crypto.spec.PBEKeySpec(pw.toCharArray(), salt, iterations, 256);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = f.generateSecret(spec).getEncoded();

            Base64.Encoder enc = Base64.getEncoder();
            return enc.encodeToString(salt) + enc.encodeToString(hash);
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new Exception();
        }
    }
    
    public static Boolean passwordMatches(Opiskelija o, String pw) {
        try {
            String salt = o.getPwHash().substring(0, 44);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] saltBytes = decoder.decode(salt);
            
            String hash = Auth.hashPassword(pw, saltBytes);
            return hash.equals(o.getPwHash());
        } catch (Exception e) {
            return false;
        }
    }
}
