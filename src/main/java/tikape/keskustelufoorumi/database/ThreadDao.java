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
public class ThreadDao {
    private Database database;
    private MessageDao messageDao;
    
    public ThreadDao(Database database) throws SQLException {
        this.database = database;
        this.messageDao = new MessageDao(database);
    }
    
    
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
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareCall(sql)) {
                s.setObject(1, key);

                try(ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("t_id");
                    Integer categoryId = rs.getInt("t_category_id");
                    String title = rs.getString("t_title");

                    Thread thread = new Thread(id, categoryId, title, null, null, null);

                    return thread;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
        
    public Integer countBy(String key, Object value) {
        try (Connection c = this.database.getConnection()) {
            try(PreparedStatement s = c.prepareStatement("SELECT COUNT(*) FROM Threads t WHERE t." + key + " = ?")) {
                s.setObject(1, value);
                
                try(ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return 0;
                    }
                    
                    return rs.getInt(1);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public List<Thread> findAllBy(String key, Object value, Integer start, Integer limit) {
        List<Thread> threads = new ArrayList();
        
        /*
        
        // vanha
        
        SELECT * FROM (
        SELECT DISTINCT ON (t.id) 
        t.id AS t_id, t.category_id AS t_category_id, t.title AS t_title, COUNT(m.id) OVER (PARTITION BY t.id) AS t_message_count, 
        m.id AS m_id, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, 
        u.id AS u_id, u.name AS u_name, u.admin AS u_admin 
        FROM Threads t 
        LEFT JOIN Messages m ON t.id = m.thread_id 
        LEFT JOIN Users u ON u.id = m.user_id 
        WHERE t.category_id = 1
        GROUP BY t.id, m.id, u.id 
        ORDER BY t.id ASC, m.sent DESC
        ) q ORDER BY m_sent DESC 

        
        // uusi... i have created a monster
        
        SELECT DISTINCT ON(t.id, m.last_sent)
        t.id AS t_id, t.title AS t_title, t.category_id AS t_category_id, t.title AS t_title, COUNT(m2.id) OVER (PARTITION BY t.id) AS t_message_count,
        m.first_id AS m_first_id, m.first_sent AT TIME ZONE 'Europe/Helsinki' AS m_first_sent, m.last_id AS m_last_id, m.last_sent AT TIME ZONE 'Europe/Helsinki' AS m_last_sent,
        uf.id AS u_first_id, ul.id AS u_last_id, uf.name AS u_first_name, ul.name AS u_last_name, uf.admin AS u_first_admin, ul.admin AS u_last_admin
        FROM Threads t
        INNER JOIN (
                SELECT
                        thread_id,
                        first_value(user_id) OVER w1 AS first_user_id,
                        first_value(id) OVER w1 AS first_id,
                        first_value(sent) OVER w1 AS first_sent,
                        first_value(user_id) OVER w2 AS last_user_id,
                        first_value(id) OVER w2 AS last_id,
                        first_value(sent) OVER w2 AS last_sent
                FROM Messages
                WINDOW
                        w1 AS (PARTITION BY thread_id ORDER BY sent ASC),
                        w2 AS (PARTITION BY thread_id ORDER BY sent DESC)
        ) m ON t.id = m.thread_id
        INNER JOIN Messages m2 ON t.id = m2.thread_id
        INNER JOIN Users uf ON uf.id = m.first_user_id
        INNER JOIN Users ul ON ul.id = m.last_user_id
        WHERE t.category_id = 1
        ORDER BY m.last_sent DESC
        LIMIT 5 OFFSET 0
        
        */
        
        /*String sql = "SELECT * FROM ("
                + "SELECT DISTINCT ON (t.id) "
                + "t.id AS t_id, t.category_id AS t_category_id, t.title AS t_title, COUNT(m.id) OVER (PARTITION BY t.id) AS t_message_count, "
                + "m.id AS m_id, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, "
                + "u.id AS u_id, u.name AS u_name, u.admin AS u_admin "
                + "FROM Threads t "
                + "LEFT JOIN Messages m ON t.id = m.thread_id "
                + "LEFT JOIN Users u ON u.id = m.user_id "
                + "WHERE t." + key + " = ? "
                + "GROUP BY t.id, m.id, u.id "
                + "ORDER BY t.id ASC, m.sent DESC"
                + ") q ORDER BY m_sent DESC "
                + "LIMIT " + limit + " OFFSET " + start;*/
        
        String sql = "SELECT DISTINCT ON(t.id, m.last_sent)\n" +
"        t.id AS t_id, t.title AS t_title, t.category_id AS t_category_id, t.title AS t_title, COUNT(m2.id) OVER (PARTITION BY t.id) AS t_message_count,\n" +
"        m.first_id AS m_first_id, m.first_sent AT TIME ZONE 'Europe/Helsinki' AS m_first_sent, m.last_id AS m_last_id, m.last_sent AT TIME ZONE 'Europe/Helsinki' AS m_last_sent,\n" +
"        uf.id AS u_first_id, ul.id AS u_last_id, uf.name AS u_first_name, ul.name AS u_last_name, uf.admin AS u_first_admin, ul.admin AS u_last_admin\n" +
"        FROM Threads t\n" +
"        INNER JOIN (\n" +
"                SELECT\n" +
"                        thread_id,\n" +
"                        first_value(user_id) OVER w1 AS first_user_id,\n" +
"                        first_value(id) OVER w1 AS first_id,\n" +
"                        first_value(sent) OVER w1 AS first_sent,\n" +
"                        first_value(user_id) OVER w2 AS last_user_id,\n" +
"                        first_value(id) OVER w2 AS last_id,\n" +
"                        first_value(sent) OVER w2 AS last_sent\n" +
"                FROM Messages\n" +
"                WINDOW\n" +
"                        w1 AS (PARTITION BY thread_id ORDER BY sent ASC),\n" +
"                        w2 AS (PARTITION BY thread_id ORDER BY sent DESC)\n" +
"        ) m ON t.id = m.thread_id\n" +
"        INNER JOIN Messages m2 ON t.id = m2.thread_id\n" +
"        INNER JOIN Users uf ON uf.id = m.first_user_id\n" +
"        INNER JOIN Users ul ON ul.id = m.last_user_id\n" +
"        WHERE t." + key + " = ?\n" +
"        ORDER BY m.last_sent DESC\n" +
"        LIMIT " + limit + " OFFSET " + start;
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setObject(1, value);

                try (ResultSet rs = s.executeQuery()) {
                    while(rs.next()) {
                        Integer uFirstId = rs.getInt("u_first_id");
                        String uFirstName = rs.getString("u_first_name");
                        Boolean uFirstAdmin = rs.getBoolean("u_first_admin");
                        
                        Integer uLastId = rs.getInt("u_last_id");
                        String uLastName = rs.getString("u_last_name");
                        Boolean uLastAdmin = rs.getBoolean("u_last_admin");

                        Integer tId = rs.getInt("t_id");
                        
                        Integer mFirstId = rs.getInt("m_first_id");
                        LocalDateTime mFirstSent = rs.getTimestamp("m_first_sent").toLocalDateTime();
                        
                        Integer mLastId = rs.getInt("m_last_id");
                        LocalDateTime mLastSent = rs.getTimestamp("m_last_sent").toLocalDateTime();

                        Integer tCategoryId = rs.getInt("t_category_id");
                        String title = rs.getString("t_title");
                        Integer messageCount = rs.getInt("t_message_count");

                        User firstUser = new User(uFirstId, uFirstName, null, uFirstAdmin);
                        User lastUser = new User(uLastId, uLastName, null, uLastAdmin);
                        
                        Message firstMessage = new Message(mFirstId, firstUser, tId, mFirstSent, null);
                        Message latestMessage = new Message(mLastId, lastUser, tId, mLastSent, null);
                        
                        Thread thread = new Thread(tId, tCategoryId, title, messageCount, firstMessage, latestMessage);

                        threads.add(thread);
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return threads;
    }
    
    public void insert(Integer categoryId, String title, Integer userId, String text) throws Exception {
        try (Connection c = this.database.getConnection()) {
            c.setAutoCommit(false);
            
            Integer threadId;
            try (PreparedStatement s = c.prepareStatement("INSERT INTO Threads (category_id, title) VALUES (?, ?) RETURNING id")) {
                s.setObject(1, categoryId);
                s.setObject(2, title);
                
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        throw new Exception();
                    }
                    
                    threadId = rs.getInt("id");
                } catch(Exception e) {
                    c.rollback();
                    c.setAutoCommit(true);
                    throw e;
                }
            }
            
            try(PreparedStatement s = c.prepareStatement("INSERT INTO Messages (user_id, thread_id, text) VALUES (?, ?, ?)")) {
                s.setObject(1, userId);
                s.setObject(2, threadId);
                s.setObject(3, text);
                
                s.executeUpdate();
                c.commit();
            } catch(Exception e) {
                c.rollback();
                
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
