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
        
        List<String> fields =  Arrays.asList("id", "user_id", "thread_id", "text");
        
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
        
        s.close();
        c.close();
        
        return viestit;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}
