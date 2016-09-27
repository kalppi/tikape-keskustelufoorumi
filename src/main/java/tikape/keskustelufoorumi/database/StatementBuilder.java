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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StatementBuilder {
    public static <K> PreparedStatement findOne(Connection connection, String table, K key) throws SQLException {
        return StatementBuilder.findOne(connection, table, key, Arrays.asList("*"));
    }
    
    public static <K> PreparedStatement findOne(Connection connection, String table, K key, List<String> fields) throws SQLException {
        PreparedStatement s = connection.prepareStatement("SELECT " + String.join(",", fields) + " FROM " + table + " WHERE id = ?");
        s.setObject(1, key);

        return s;
    }
    
    public static <K> PreparedStatement findAll(Connection connection, String table) throws SQLException {
        return StatementBuilder.findAll(connection, table, Arrays.asList("*"));
    }
    
    public static <K> PreparedStatement findAll(Connection connection, String table, List<String> fields) throws SQLException {
        PreparedStatement s = connection.prepareStatement("SELECT " + String.join(",", fields) + " FROM " + table);
        
        return s;
    }
    
    public static <K> PreparedStatement findAllIn(Connection connection, String table, Collection<K> keys) throws SQLException {
        return StatementBuilder.findAllIn(connection, table, keys, Arrays.asList("*"));
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
    
    public static LocalDateTime getDate(ResultSet rs, String key) {
        LocalDateTime date = null;

        try {
            date = LocalDateTime.parse(rs.getString(key), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch(SQLException e) {
            
        }
        
        return date;
    }
}
