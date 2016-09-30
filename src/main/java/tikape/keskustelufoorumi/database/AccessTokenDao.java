package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.domain.AccessToken;


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
            PreparedStatement s = StatementBuilder.findOneBy(c, "Access_tokens", key, value, Arrays.asList("*"));
            
            ResultSet rs = s.executeQuery();
            if(!rs.next()) {
                return null;
            }

            Integer id = rs.getInt("id");
            String name = rs.getString("token");
            Integer userId = rs.getInt("user_id");

            AccessToken t = new AccessToken(id, name, userId);

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
    
    public void insert(String token, Integer userId) throws SQLException {
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.insert(c, "Access_tokens", Arrays.asList("token", "user_id"));
        
        s.setObject(1, token);
        s.setObject(2, userId);
        
        s.execute();
    }
}
