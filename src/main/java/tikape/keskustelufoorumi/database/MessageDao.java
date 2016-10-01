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
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = StatementBuilder.findOne(c, "Messages", key, Arrays.asList("*"));
            ResultSet rs = s.executeQuery();

            if(!rs.next()) {
                return null;
            }

            Integer id = rs.getInt("id");
            Integer userId = rs.getInt("user_id");
            Integer threadId = rs.getInt("thread_id");
            String text = rs.getString("text");
            String sqlDate = rs.getString("sent");
            LocalDateTime sent = Helper.parseSqlDate(sqlDate);
            
            rs.close();
            s.close();
            c.close();

            User opiskelija = this.userDao.findOne(userId);

            return new Message(id, opiskelija, threadId, sent, text);
        } catch(SQLException e) {
            return null;
        }
    }
    
    @Override
    public Message findOneBy(String key, Object value) {
        return null;
    }
    
    @Override
    public List<Message> findAll() throws SQLException {
        return null;
    }
    
    @Override
    public List<Message> findAllIn(Collection<Integer> keys) throws SQLException {
        if(keys.isEmpty()) {
            return new ArrayList();
        }
        
        List<Message> viestit = new ArrayList();
        
        Connection c = this.database.getConnection();
        
        List<String> fields = new ArrayList(Arrays.asList("id", "user_id", "thread_id", "text"));
        
        if(this.database.isPostgres()) {
            fields.add("sent AT TIME ZONE 'Europe/Helsinki'");
        } else {
            fields.add("DATETIME(sent, 'localtime') AS sent");
        }
        
        PreparedStatement s = StatementBuilder.findAllIn(c, "Messages", keys, fields);
        
        ResultSet rs = s.executeQuery();
        
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
        
        rs.close();
        s.close();
        c.close();
        
        return viestit;
    }
    
    public Map<Integer, Message> findLatestInCategories(Collection<Integer> keys) throws SQLException {
        StringBuilder str = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            str.append(", ?");
        }
        
        List<String> fields =  new ArrayList(Arrays.asList("m.id", "m.user_id", "m.thread_id", "m.text", "t.category_id"));
        
        if(this.database.isPostgres()) {
            fields.add("m.sent AT TIME ZONE 'Europe/Helsinki' AS sent");
        } else {
            fields.add("DATETIME(m.sent, 'localtime') AS sent");
        }
        
        String sql = "SELECT " + String.join(",", fields) + " " +
            "FROM (SELECT * FROM Messages ORDER BY sent ASC) m " +
            "LEFT JOIN Threads t ON t.id = m.thread_id " +
            "WHERE t.category_id IN (" + str + ") " +
            "GROUP BY t.category_id, m.id, m.user_id, m.thread_id, m.text, m.sent";
        
        System.out.println(sql);
        
        Connection c = this.database.getConnection();
        PreparedStatement s = c.prepareStatement(sql);
        
        int i = 1;
        for(Integer key : keys) {
            s.setObject(i++, key);
        }
        
        ResultSet rs = s.executeQuery();
        
        Map<Integer, Message> messages = new HashMap();
        Map<Integer, List<Message>> userMap = new HashMap();
        
        while(rs.next()) {
            Integer id = rs.getInt("id");
            Integer userId = rs.getInt("user_id");
            Integer threadId = rs.getInt("thread_id");
            String text = rs.getString("text");
            
            LocalDateTime sent = null;
            if(this.database.isPostgres()) {
                sent = rs.getTimestamp("sent").toLocalDateTime();
            } else {
                String sqlDate = rs.getString("sent");
                sent = Helper.parseSqlDate(sqlDate);
            }
              
            Integer categoryId = rs.getInt("category_id");
            
            Message message = new Message(id, null, threadId, sent, text);
            messages.put(categoryId, message);
            
            if(!userMap.containsKey(userId)) {
                userMap.put(userId, new ArrayList());
            }
            
            userMap.get(userId).add(message);
        }
        
        List<User> users = this.userDao.findAllIn(userMap.keySet());
        
        for(User u : users) {
            for(Message m : userMap.get(u.getId())) {
                m.setUser(u);
            }
        }
        
        rs.close();
        s.close();
        c.close();
        
        return messages;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}
