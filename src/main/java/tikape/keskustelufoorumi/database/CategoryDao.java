package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.Helper;
import tikape.keskustelufoorumi.domain.Category;
import tikape.keskustelufoorumi.domain.Message;
import tikape.keskustelufoorumi.domain.User;

public class CategoryDao {
    private Database database;
    private MessageDao messageDao;

    public CategoryDao(Database database) throws SQLException {
        this.database = database;
        this.messageDao = new MessageDao(database);
    }
    
    public Category findOne(Integer id) {
        return this.findOneBy("id", id);
    }
    
    public Category findOneBy(String key, Object value) {
        String sql = "SELECT DISTINCT ON(c.id) "
                + "c.id AS c_id, c.name AS c_name, COUNT(m.id) OVER (PARTITION BY c.id) AS c_message_count, "
                + "m.id AS m_id, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, "
                + "u.id AS u_id, u.name AS u_name, u.admin AS u_admin, "
                + "t.id AS t_id "
                + "FROM Categories c "
                + "LEFT JOIN Threads t ON t.category_id = c.id "
                + "LEFT JOIN Messages m ON m.thread_id = t.id "
                + "LEFT JOIN Users u ON u.id = m.user_id "
                + "WHERE c." + key + " = ? "
                + "GROUP BY c.id, m.id, u.id, t.id "
                + "ORDER BY c.id ASC, m.sent DESC";
        
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setObject(1, value);

                try (ResultSet rs = s.executeQuery()) {
                    if(!rs.next()) {
                        return null;
                    }

                    Integer id = rs.getInt("c_id");
                    String name = rs.getString("c_name");
                    Integer messageCount = rs.getInt("c_message_count");

                    Category cat = new Category(id, name, messageCount, null);

                    return cat;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT DISTINCT ON(c.name)"
            + "c.id AS c_id, c.name AS c_name, COUNT(m.id) OVER (PARTITION BY c.id) AS c_message_count, "
            + "m.id AS m_id, m.sent AT TIME ZONE 'Europe/Helsinki' AS m_sent, "
            + "u.id AS u_id, u.name AS u_name, u.admin AS u_admin, "
            + "t.id AS t_id "
            + "FROM Categories c "
            + "LEFT JOIN Threads t ON t.category_id = c.id "
            + "LEFT JOIN Messages m ON m.thread_id = t.id "
            + "LEFT JOIN Users u ON u.id = m.user_id " 
            + "GROUP BY c.id, m.id, u.id, t.id "
            + "ORDER BY c.name ASC, m.sent DESC;";
            
        try (Connection c = this.database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(sql)) {
                try (ResultSet rs = s.executeQuery()) {
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
                            sent = rs.getTimestamp("m_sent").toLocalDateTime();
                        }

                        User user = new User(uId, uName, null, uAdmin);
                        Message message = new Message(mId, user, tId, sent, null);
                        Category category = new Category(cId, cName, cMessageCount, message);

                        categories.add(category);
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }
}