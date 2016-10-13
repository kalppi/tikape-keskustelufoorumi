package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.Auth;
import tikape.keskustelufoorumi.domain.User;

public class UserDao {
    private Database database;

    public UserDao(Database database) throws SQLException {
        this.database = database;
    }

    public User findOne(Integer key) {   
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.findOne(c, "Users", key, Arrays.asList("*"))) {
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("id");
                    String name = rs.getString("name");
                    String pwHash = rs.getString("pw_hash");
                    Boolean admin = rs.getBoolean("admin");

                    User o = new User(id, name, pwHash, admin);

                    return o;
                }
            }
        } catch(SQLException e) {
            return null;
        }
    }
    
    public User findOneBy(String key, Object value) {
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.findOneBy(c, "Users", key, value, Arrays.asList("*"))) {
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("id");
                    String name = rs.getString("name");
                    String pwHash = rs.getString("pw_hash");
                    Boolean admin = rs.getBoolean("admin");

                    User o = new User(id, name, pwHash, admin);

                    return o;
                }
            }
        } catch(SQLException e) {
            return null;
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.findAll(c, "Users", Arrays.asList("*"))) {
                try (ResultSet rs = s.executeQuery()) {
                    while(rs.next()) {
                        Integer id = rs.getInt("id");
                        String name = rs.getString("name");
                        String pwHash = rs.getString("pw_hash");
                        Boolean admin = rs.getBoolean("admin");

                        users.add(new User(id, name, pwHash, admin));
                    }

                    return users;
                }
            }
        } catch(SQLException e) {
            return users;
        }
    }
    
    public List<User> findAll(Integer start, Integer limit) {
        List<User> users = new ArrayList<>();
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.findAll(c, "Users", Arrays.asList("*"), start, limit, "id" )) {
                try (ResultSet rs = s.executeQuery()) {
                    while(rs.next()) {
                        Integer id = rs.getInt("id");
                        String name = rs.getString("name");
                        String pwHash = rs.getString("pw_hash");
                        Boolean admin = rs.getBoolean("admin");

                        users.add(new User(id, name, pwHash, admin));
                    }

                    return users;
                }
            }
        } catch(SQLException e) {
            return users;
        }
    }
    
    public List<User> findAllIn(Collection<Integer> keys) {
        if(keys.isEmpty()) {
            return new ArrayList();
        }
        
        List<User> users = new ArrayList();
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.findAllIn(c, "Users", keys, Arrays.asList("*"))) {
                try (ResultSet rs = s.executeQuery()) {
                    if(rs == null) {
                        return new ArrayList();
                    }

                    while(rs.next()) {
                        Integer id = rs.getInt("id");
                        String name = rs.getString("name");
                        String pwHash = rs.getString("pw_hash");
                        Boolean admin = rs.getBoolean("admin");

                        users.add(new User(id, name, pwHash, admin));
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
        
    public void insert(String name, String pw, Boolean admin) throws Exception {
        String pwHash = Auth.hashPassword(pw);
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.insert(c, "Users", Arrays.asList("name", "pw_hash", "admin"))) {
                s.setObject(1, name);
                s.setObject(2, pwHash);
                s.setObject(3, admin);

                s.execute();
            }
        } catch(SQLException e) {
            e.printStackTrace();
            
            throw e;
        }
    }
}
