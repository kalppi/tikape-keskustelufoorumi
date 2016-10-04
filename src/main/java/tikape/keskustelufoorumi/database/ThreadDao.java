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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tikape.keskustelufoorumi.domain.Thread;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Thread> findAllBy(String key, Object value) {
        List<Thread> threads = null;
        
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
 
        
        return threads;
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

    @Override
    public Thread findOneBy(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
