package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return null;
    }
    
    @Override
    public Category findOneBy(String key, Object value) {
        return null;
    }
    
    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = c.prepareStatement("SELECT a.id, a.name, COUNT(v.id) AS message_count " +
                "FROM Categories a " +
                "LEFT JOIN Threads k ON k.category_id = a.id " +
                "LEFT JOIN Messages v ON k.id = v.thread_id " +
                "GROUP BY a.id ORDER BY a.id ASC;");

            ResultSet rs = s.executeQuery();
            
            Map<Integer, Category> categoryMap = new HashMap();
            
            while(rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                Integer messageCount = rs.getInt("message_count");

                Category category = new Category(id, name, messageCount, null);
                categories.add(category);
                
                categoryMap.put(id, category);
            }
            
            Map<Integer, Message> messages = this.messageDao.findLatestInCategories(categoryMap.keySet());
            
            for(Map.Entry<Integer, Message> m : messages.entrySet()) {
                categoryMap.get(m.getKey()).setLatestMessage(m.getValue());
            }

            s.close();
            c.close();

            return categories;
        } catch(SQLException e) {
            e.printStackTrace();
            
            return categories;
        }
    }
    
    @Override
    public List<Category> findAllIn(Collection<Integer> keys) throws SQLException {
        return null;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}