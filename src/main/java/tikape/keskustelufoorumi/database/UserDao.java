package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM Users WHERE id = ?")) {
                s.setObject(1, key);
                
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("id");
                    String name = rs.getString("name");
                    String pwHash = rs.getString("pw_hash");
                    Boolean admin = rs.getBoolean("admin");
                    LocalDateTime registered = rs.getTimestamp("registered").toLocalDateTime();

                    User o = new User(id, name, pwHash, admin, registered);

                    return o;
                }
            }
        } catch(SQLException e) {
            return null;
        }
    }
    
    public User findOneBy(String key, Object value) {
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM Users WHERE " + key + " = ?")) {
                s.setObject(1, value);
                
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("id");
                    String name = rs.getString("name");
                    String pwHash = rs.getString("pw_hash");
                    Boolean admin = rs.getBoolean("admin");
                    LocalDateTime registered = rs.getTimestamp("registered").toLocalDateTime();

                    User o = new User(id, name, pwHash, admin, registered);

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
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM Users ORDER BY id ASC")) {
                try (ResultSet rs = s.executeQuery()) {
                    while(rs.next()) {
                        Integer id = rs.getInt("id");
                        String name = rs.getString("name");
                        String pwHash = rs.getString("pw_hash");
                        Boolean admin = rs.getBoolean("admin");
                        LocalDateTime registered = rs.getTimestamp("registered").toLocalDateTime();

                        users.add(new User(id, name, pwHash, admin, registered));
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
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM Users ORDER BY id ASC LIMIT ? OFFSET ?")) {
                s.setObject(1, limit);
                s.setObject(2, start);
                
                try (ResultSet rs = s.executeQuery()) {
                    while(rs.next()) {
                        Integer id = rs.getInt("id");
                        String name = rs.getString("name");
                        String pwHash = rs.getString("pw_hash");
                        Boolean admin = rs.getBoolean("admin");
                        LocalDateTime registered = rs.getTimestamp("registered").toLocalDateTime();

                        users.add(new User(id, name, pwHash, admin, registered));
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
        
        StringBuilder str = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            str.append(", ?");
        }
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM Users WHERE id IN (" + str + ")")) {
                int i = 1;
                for(Integer key : keys) {
                    s.setObject(i++, key);
                }
                
                try (ResultSet rs = s.executeQuery()) {
                    if(rs == null) {
                        return new ArrayList();
                    }

                    while(rs.next()) {
                        Integer id = rs.getInt("id");
                        String name = rs.getString("name");
                        String pwHash = rs.getString("pw_hash");
                        Boolean admin = rs.getBoolean("admin");
                        LocalDateTime registered = rs.getTimestamp("registered").toLocalDateTime();

                        users.add(new User(id, name, pwHash, admin, registered));
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
            try (PreparedStatement s = c.prepareStatement("INSERT INTO Users (name, pw_hash, admin) VALUES (?, ?, ?)")) {
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
