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
public class AccessTokenDao {
    private Database database;

    public AccessTokenDao(Database database) {
        this.database = database;
    }
    
    public AccessToken findOneBy(String key, Object value) {
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM Access_tokens WHERE " + key + " = ?")) {
                s.setObject(1, value);
                
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("id");
                    String name = rs.getString("token");
                    Integer userId = rs.getInt("user_id");

                    AccessToken t = new AccessToken(id, name, userId);

                    return t;
                }
            }
        } catch(SQLException e) {
            return null;
        }
    }
    
    public void delete(Integer key) {
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement("DELETE FROM Access_tokens WHERE id = ?")) {
                s.setObject(1, key);
                
                s.execute();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void insert(String token, Integer userId) throws SQLException {
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s =  c.prepareStatement("INSERT INTO Access_tokens (token, user_id) VALUES (?, ?)")) {
                s.setObject(1, token);
                s.setObject(2, userId);

                s.execute();
            }
        } catch(SQLException e) {
            e.printStackTrace();
            
            throw e;
        }
    }
}
