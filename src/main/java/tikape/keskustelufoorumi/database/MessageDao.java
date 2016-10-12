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
import tikape.keskustelufoorumi.domain.User;
import tikape.keskustelufoorumi.domain.Message;

public class MessageDao implements IDao<Message, Integer> {
    private Database database;
    private UserDao userDao;

    public MessageDao(Database database) throws SQLException {
        this.database = database;
        this.userDao = new UserDao(this.database);
    }
    
    @Override
    public Message findOne(Integer key) {
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.findOne(c, "Messages", key, Arrays.asList("*"))) {
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("id");
                    Integer userId = rs.getInt("user_id");
                    Integer threadId = rs.getInt("thread_id");
                    String text = rs.getString("text");
                    String sqlDate = rs.getString("sent");
                    LocalDateTime sent = Helper.parseSqlDate(sqlDate);

                    User user = this.userDao.findOne(userId);

                    return new Message(id, user, threadId, sent, text);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
            
            return null;
        }
    }
    
    @Override
    public Message findOneBy(String key, Object value) {
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = StatementBuilder.findOneBy(c, "Messages", key, value, Arrays.asList("*"))) {
                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("id");
                    Integer userId = rs.getInt("user_id");
                    Integer threadId = rs.getInt("thread_id");
                    String text = rs.getString("text");
                    String sqlDate = rs.getString("sent");
                    LocalDateTime sent = Helper.parseSqlDate(sqlDate);

                    User user = this.userDao.findOne(userId);

                    return new Message(id, user, threadId, sent, text);
                }
            }
        } catch(SQLException e) {
            return null;
        }
    }
    
    @Override
    public List<Message> findAllIn(Collection<Integer> keys) {
        if(keys.isEmpty()) {
            return new ArrayList();
        }
        
        List<Message> viestit = new ArrayList();
        
        try (Connection c = this.database.getConnection()) {
            List<String> fields = new ArrayList(Arrays.asList("id", "user_id", "thread_id", "text"));

            if(this.database.isPostgres()) {
                fields.add("sent AT TIME ZONE 'Europe/Helsinki'");
            } else {
                fields.add("DATETIME(sent, 'localtime') AS sent");
            }

            try (PreparedStatement s = StatementBuilder.findAllIn(c, "Messages", keys, fields)) {
                try (ResultSet rs = s.executeQuery()) {
                    if(rs == null) {
                        return new ArrayList();
                    }

                    Map<Integer, List<Message>> userMap = new HashMap();

                    while(rs.next()) {
                        Integer id = rs.getInt("id");
                        Integer userId = rs.getInt("user_id");
                        Integer threadId = rs.getInt("thread_id");
                        String text = rs.getString("text");
                        String sqlDate = rs.getString("sent");
                        LocalDateTime sent = Helper.parseSqlDate(sqlDate);

                        Message message = new Message(id, null, threadId, sent, text);
                        viestit.add(message);

                        if(!userMap.containsKey(userId)) {
                            userMap.put(userId, new ArrayList());
                        }

                        userMap.get(userId).add(message);
                    }

                    List<User> users = this.userDao.findAllIn(userMap.keySet());

                    for(User o : users) {
                        for(Message v : userMap.get(o.getId())) {
                            v.setUser(o);
                        }
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return viestit;
    }
    
    public List<Message> findAllInThread(Integer id) {
        List<Message> messages = new ArrayList();
        String sql = "SELECT "
                + "m.id AS m_id, m.text AS m_text, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, m.thread_id AS m_thread_id, "
                + "u.id AS u_id, u.name AS u_name, u.admin AS u_admin "
                + "FROM Messages m "
                + "LEFT JOIN Users u ON u.id = m.user_id "
                + "WHERE m.thread_id = ? "
                + "ORDER BY m.sent ASC";
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setObject(1, id);

                try (ResultSet rs = s.executeQuery()) {
                    while(rs.next()) {
                        Integer mId = rs.getInt("m_id");
                        Integer mThreadId = rs.getInt("m_thread_id");
                        LocalDateTime mSent = rs.getTimestamp("m_sent").toLocalDateTime();
                        String mText = rs.getString("m_text");

                        Integer uId = rs.getInt("u_id");
                        String uName = rs.getString("u_name");
                        Boolean uAdmin = rs.getBoolean("u_admin");

                        User user = new User(uId, uName, null, uAdmin);
                        Message message = new Message(mId, user, mThreadId, mSent, mText);

                        messages.add(message);
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return messages;
    }
    
    @Override
    public void delete(Integer key) {
        // ei toteutettu
    }

    @Override
    public List<Message> findAllBy(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void insert(Integer threadId, Integer userId, String text) throws Exception {
        try (Connection c = this.database.getConnection()) {
            try(PreparedStatement s = c.prepareStatement("INSERT INTO Messages (user_id, thread_id, text) VALUES (?, ?, ?)")) {
                s.setObject(1, userId);
                s.setObject(2, threadId);
                s.setObject(3, text);
                
                s.executeUpdate();
            }
        }
    }
}
