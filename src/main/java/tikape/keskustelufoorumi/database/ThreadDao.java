package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.domain.Message;
import tikape.keskustelufoorumi.domain.Thread;
import tikape.keskustelufoorumi.domain.User;

/**
 *
 * @author jarnoluu
 */
public class ThreadDao implements IDao<Thread, Integer>  {
    private Database database;
    
    public ThreadDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Thread findOne(Integer key) {
        String sql = "SELECT DISTINCT ON (t.id) "
                + "t.id AS t_id, t.category_id AS t_category_id, t.title AS t_title, COUNT(m.id) OVER (PARTITION BY t.id) AS t_message_count "
                //+ "m.id AS m_id, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, "
                //+ "u.id AS u_id, u.name AS u_name, u.admin AS u_admin "
                + "FROM Threads t "
                + "LEFT JOIN Messages m ON t.id = m.thread_id "
                //+ "LEFT JOIN Users u ON u.id = m.user_id "
                + "WHERE t.id = ? "
                + "GROUP BY t.id, m.id "
                + "ORDER BY t.id ASC, m.sent DESC";
        
        try {
            Connection c = this.database.getConnection();
            
            PreparedStatement s = c.prepareCall(sql);
            s.setObject(1, key);
            
            ResultSet rs = s.executeQuery();
            
            if(!rs.next()) {
                return null;
            }
            
            Integer id = rs.getInt("t_id");
            Integer categoryId = rs.getInt("t_category_id");
            String title = rs.getString("t_title");
            
            Thread thread = new Thread(id, categoryId, title, null, null);
            
            return thread;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Thread> findAllBy(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        /*List<Thread> threads = null;
        
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = c.prepareStatement("SELECT t.id, t.title, t.category_id " +
                    "LEFT JOIN Messages m ON t.id = m.thread_id" +
                    "FROM Threads t " +
                    "WHERE t." + key + " = ?" +
                    "GROUP BY t.id ORDER BY m.sent DESC"
            );
            
            s.setObject(1, value);

            ResultSet rs = s.executeQuery();
            
            Map<Integer, Thread> threadMap = new HashMap();

            while(rs.next()) {
                Integer id = rs.getInt("id");
                String title = rs.getString("title");
                Integer categoryId = rs.getInt("category_id");
                
                Thread thread = new Thread(id, categoryId, title, null);
                
                threads.add(thread);
                
                threadMap.put(id, thread);
            }
            
                       
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        
        return threads;*/
    }

    @Override
    public List<Thread> findAllIn(Collection<Integer> keys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Thread> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public List<Thread> findAllInCategory(Integer cat) {
        List<Thread> threads = new ArrayList();
        
        String sql = "SELECT * FROM ("
                + "SELECT DISTINCT ON (t.id) "
                + "t.id AS t_id, t.category_id AS t_category_id, t.title AS t_title, COUNT(m.id) OVER (PARTITION BY t.id) AS t_message_count, "
                + "m.id AS m_id, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, "
                + "u.id AS u_id, u.name AS u_name, u.admin AS u_admin "
                + "FROM Threads t "
                + "LEFT JOIN Messages m ON t.id = m.thread_id "
                + "LEFT JOIN Users u ON u.id = m.user_id "
                + "WHERE t.category_id = ? "
                + "GROUP BY t.id, m.id, u.id "
                + "ORDER BY t.id ASC, m.sent DESC"
                + ") q ORDER BY m_sent DESC";
        
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            
            s.setObject(1, cat);
            
            ResultSet rs = s.executeQuery();
            
            while(rs.next()) {
                Integer uId = rs.getInt("u_id");
                String uName = rs.getString("u_name");
                Boolean uAdmin = rs.getBoolean("u_admin");
                
                Integer mId = rs.getInt("m_id");
                Integer tId = rs.getInt("t_id");
                LocalDateTime sent = rs.getTimestamp("m_sent").toLocalDateTime();
                
                Integer tCategoryId = rs.getInt("t_category_id");
                String title = rs.getString("t_title");
                Integer messageCount = rs.getInt("t_message_count");
                
                User user = new User(uId, uName, null, uAdmin);
                Message message = new Message(mId, user, tId, sent, null);
                Thread thread = new Thread(tId, tCategoryId, title, messageCount, message);
                
                threads.add(thread);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return threads;
    }

    @Override
    public Thread findOneBy(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
