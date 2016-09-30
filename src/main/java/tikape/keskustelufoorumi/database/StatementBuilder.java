package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class StatementBuilder {
    public static <K> PreparedStatement findOne(Connection connection, String table, K key, List<String> fields) throws SQLException {
        PreparedStatement s = connection.prepareStatement("SELECT " + String.join(",", fields) + " FROM " + table + " WHERE id = ?");
        s.setObject(1, key);

        return s;
    }
    
    public static PreparedStatement findOneBy(Connection connection, String table, String key, Object value, List<String> fields) throws SQLException {
        PreparedStatement s = connection.prepareStatement("SELECT " + String.join(",", fields) + " FROM " + table + " WHERE " + key + " = ?");
        s.setObject(1, value);

        return s;
    }
    
    public static <K> PreparedStatement findAll(Connection connection, String table, List<String> fields) throws SQLException {
        return connection.prepareStatement("SELECT " + String.join(",", fields) + " FROM " + table + " ORDER BY id ASC");
    }
    
    public static <K> PreparedStatement findAll(Connection connection, String table, List<String> fields, Integer start, Integer limit, String orderField) throws SQLException {
        return connection.prepareStatement("SELECT " + String.join(",", fields) + " FROM " + table + " ORDER BY " + orderField + " ASC LIMIT " + limit + " OFFSET " + start);
    }
    
    public static <K> PreparedStatement findAllIn(Connection connection, String table, Collection<K> keys, List<String> fields) throws SQLException {
        StringBuilder str = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            str.append(", ?");
        }
        
        PreparedStatement s = connection.prepareStatement("SELECT " + String.join(",", fields) + " FROM " + table + " WHERE id IN (" + str + ")");
        
        int i = 1;
        for(K key : keys) {
            s.setObject(i++, key);
        }
        
        return s;
    }
    
    public static PreparedStatement insert(Connection connection, String table, List<String> fields) throws SQLException {
        StringBuilder str = new StringBuilder("?");
        for (int i = 1; i < fields.size(); i++) {
            str.append(", ?");
        }
        
        PreparedStatement s = connection.prepareStatement("INSERT INTO " + table + "(" + String.join(",", fields) + ") VALUES(" + str + ")");
        
        return s;
    }
    
    public static <K> PreparedStatement delete(Connection connection, String table, K key) throws SQLException {
        PreparedStatement s = connection.prepareStatement("DELETE FROM " + table + " WHERE id = ?");
        s.setObject(1, key);
        
        return s;
    }
}
