package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tikape.keskustelufoorumi.Helper;
import tikape.keskustelufoorumi.domain.Category;
import tikape.keskustelufoorumi.domain.Message;
import tikape.keskustelufoorumi.domain.User;

public class CategoryDao implements IDao<Category, Integer> {
    private Database database;
    private MessageDao messageDao;

    public CategoryDao(Database database) throws SQLException {
        this.database = database;
        this.messageDao = new MessageDao(database);
    }
    
    @Override
    public Category findOne(Integer key) {
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = c.prepareStatement("SELECT a.id, a.name, COUNT(v.id) AS message_count " +
                    "FROM Categories a " +
                    "LEFT JOIN Threads k ON k.category_id = a.id " +
                    "LEFT JOIN Messages v ON k.id = v.thread_id " +
                    "WHERE a.id = ?");
            
            s.setObject(1, key);
            
            ResultSet rs = s.executeQuery();
            
            if(!rs.next()) {
                return null;
            }
            
            Integer id = rs.getInt("id");
            String name = rs.getString("name");
            Integer messageCount = rs.getInt("message_count");
            
            Category cat = new Category(id, name, messageCount, null);
            
            return cat;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public Category findOneBy(String key, Object value) {
        return null;
    }
    
    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();

        String sql = null;
        
        if(this.database.isPostgres()) {
            sql = "SELECT DISTINCT ON(c.id)"
                    + "c.id AS c_id, c.name AS c_name, COUNT(m.id) OVER (PARTITION BY c.id) AS c_message_count, "
                    + "m.id AS m_id, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, "
                    + "u.id AS u_id, u.name AS u_name, u.admin AS u_admin, "
                    + "t.id AS t_id "
                    + "FROM Categories c "
                    + "LEFT JOIN Threads t ON t.category_id = c.id "
                    + "LEFT JOIN Messages m ON m.thread_id = t.id "
                    + "LEFT JOIN Users u ON u.id = m.user_id " 
                    + "GROUP BY c.id, m.id, u.id, t.id "
                    + "ORDER BY c.id ASC, m.sent DESC;";
        } else {
           sql = "SELECT "
                + "c.id AS c_id, c.name AS c_name, COUNT(m.id) AS c_message_count, "
                + "m.id AS m_id, u.id AS u_id, u.name AS u_name, u.admin AS u_admin, t.id AS t_id, "
                + "DATETIME(MAX(m.sent), 'localtime') AS m_sent "
                + "FROM Categories c "
                + "LEFT JOIN Threads t ON t.category_id = c.id "
                + "LEFT JOIN Messages m ON m.thread_id = t.id "
                + "LEFT JOIN Users u ON u.id = m.user_id "
                + "GROUP BY c.id "
                + "ORDER BY c.id ASC";
        }
                
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            
            ResultSet rs = s.executeQuery();
            
            while(rs.next()) {
                Integer cId = rs.getInt("c_id");
                String cName = rs.getString("c_name");
                Integer cMessageCount = rs.getInt("c_message_count");
                                
                Integer uId = rs.getInt("u_id");
                String uName = rs.getString("u_name");
                Boolean uAdmin = rs.getBoolean("u_admin");
                
                Integer tId = rs.getInt("t_id");
                
                Integer mId = rs.getInt("m_id");
                
                LocalDateTime sent = null;
                
                if(tId != 0) {                    
                    if(this.database.isPostgres()) {
                        sent = rs.getTimestamp("m_sent").toLocalDateTime();
                    } else {
                        String sqlDate = rs.getString("m_sent");
                        sent = Helper.parseSqlDate(sqlDate);
                    }
                }
                
                User user = new User(uId, uName, null, uAdmin);
                Message message = new Message(mId, user, tId, sent, null);
                Category category = new Category(cId, cName, cMessageCount, message);
                
                categories.add(category);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }
    
    @Override
    public List<Category> findAllIn(Collection<Integer> keys) throws SQLException {
        return null;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }

    @Override
    public List<Category> findAllBy(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}