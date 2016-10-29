package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tikape.keskustelufoorumi.domain.User;
import tikape.keskustelufoorumi.domain.Message;

public class MessageDao {
    private Database database;
    private UserDao userDao;

    public MessageDao(Database database) throws SQLException {
        this.database = database;
        this.userDao = new UserDao(this.database);
    }
        
    public List<Message> findAllBy(String key, Object value, Integer start, Integer limit) {
        List<Message> messages = new ArrayList();
        String sql = "SELECT "
                + "m.id AS m_id, m.text AS m_text, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, m.thread_id AS m_thread_id, "
                + "u.id AS u_id, u.name AS u_name, u.admin AS u_admin, u.registered AS u_registered "
                + "FROM Messages m "
                + "LEFT JOIN Users u ON u.id = m.user_id "
                + "WHERE m." + key + " = ? "
                + "ORDER BY m.sent ASC "
                + "LIMIT ? OFFSET ?";
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setObject(1, value);
                s.setObject(2, limit);
                s.setObject(3, start);

                try (ResultSet rs = s.executeQuery()) {
                    while(rs.next()) {
                        Integer mId = rs.getInt("m_id");
                        Integer mThreadId = rs.getInt("m_thread_id");
                        LocalDateTime mSent = rs.getTimestamp("m_sent").toLocalDateTime();
                        String mText = rs.getString("m_text");

                        Integer uId = rs.getInt("u_id");
                        String uName = rs.getString("u_name");
                        Boolean uAdmin = rs.getBoolean("u_admin");
                        LocalDateTime uRegistered = rs.getTimestamp("u_registered").toLocalDateTime();

                        User user = new User(uId, uName, null, uAdmin, uRegistered);
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
    
    public Integer countBy(String key, Object value) {
        try (Connection c = this.database.getConnection()) {
            try(PreparedStatement s = c.prepareStatement("SELECT COUNT(*) FROM Messages t WHERE t." + key + " = ?")) {
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
}
