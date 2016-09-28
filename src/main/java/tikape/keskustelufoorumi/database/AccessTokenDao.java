/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.database;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.domain.AccessToken;
import tikape.keskustelufoorumi.domain.Opiskelija;

/**
 *
 * @author jarno
 */
public class AccessTokenDao implements IDao<AccessToken, Integer> {
        private Database database;

    public AccessTokenDao(Database database) {
        this.database = database;
    }
    
    @Override
    public AccessToken findOne(Integer key) {
        return null;
    }
    
    @Override
    public AccessToken findOneBy(String key, Object value) {
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = StatementBuilder.findOneBy(c, "Access_tokens", key, value);
            
            ResultSet rs = s.executeQuery();
            if(!rs.next()) {
                return null;
            }

            Integer id = rs.getInt("id");
            String nimi = rs.getString("token");
            Integer opiskelijaId = rs.getInt("opiskelija_id");

            AccessToken t = new AccessToken(id, nimi, opiskelijaId);

            s.close();
            c.close();

            return t;
        } catch(SQLException e) {
            return null;
        }
    }
    
    @Override
    public List<AccessToken> findAll() throws SQLException {
        return null;
    }
    
    @Override
    public List<AccessToken> findAllIn(Collection<Integer> keys) throws SQLException {
        return null;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.delete(c, "Access_tokens", key);
        s.execute();
    }
    
    public void insert(String token, Integer opiskelijaId) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.insert(c, "Access_tokens", Arrays.asList("token", "opiskelija_id"));
        
        s.setObject(1, token);
        s.setObject(2, opiskelijaId);
        
        s.execute();
    }
}
