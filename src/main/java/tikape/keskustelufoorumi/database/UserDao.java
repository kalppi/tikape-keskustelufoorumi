/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

public class UserDao implements IDao<User, Integer> {
    private Database database;

    public UserDao(Database database) throws SQLException {
        this.database = database;
    }

    @Override
    public User findOne(Integer key) {   
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = StatementBuilder.findOne(c, "Users", key);

            ResultSet rs = s.executeQuery();
            if(!rs.next()) {
                return null;
            }

            Integer id = rs.getInt("id");
            String name = rs.getString("name");
            String pwHash = rs.getString("pw_hash");

            User o = new User(id, name, pwHash);

            s.close();
            c.close();

            return o;
        } catch(SQLException e) {
            return null;
        }
    }
    
    @Override
    public User findOneBy(String key, Object value) {
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = StatementBuilder.findOneBy(c, "Users", key, value);

            ResultSet rs = s.executeQuery();
            if(!rs.next()) {
                return null;
            }

            Integer id = rs.getInt("id");
            String name = rs.getString("name");
            String pwHash = rs.getString("pw_hash");

            User o = new User(id, name, pwHash);

            s.close();
            c.close();

            return o;
        } catch(SQLException e) {
            return null;
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.findAll(c, "Users");

        ResultSet rs = s.executeQuery();
        List<User> users = new ArrayList<>();
        while(rs.next()) {
            Integer id = rs.getInt("id");
            String name = rs.getString("name");
            String pwHash = rs.getString("pw_hash");

            users.add(new User(id, name, pwHash));
        }
        
        s.close();
        c.close();

        return users;
    }
    
    @Override
    public List<User> findAllIn(Collection<Integer> keys) throws SQLException {
        if(keys.isEmpty()) {
            return new ArrayList();
        }
        
        List<User> users = new ArrayList();
        
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.findAllIn(c, "Users", keys);
        ResultSet rs = s.executeQuery();
        
        if(rs == null) {
            return new ArrayList();
        }
        
        while(rs.next()) {
            Integer id = rs.getInt("id");
            String name = rs.getString("name");
            String pwHash = rs.getString("pw_hash");

            users.add(new User(id, name, pwHash));
        }
        
        s.close();
        c.close();
        
        return users;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
    
    public void insert(String name, String pw) throws Exception {
        String pwHash = Auth.hashPassword(pw);
        
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.insert(c, "Users", Arrays.asList("name", "pw_hash"));
        
        s.setObject(1, name);
        s.setObject(2, pwHash);
        
        s.execute();
    }
}
