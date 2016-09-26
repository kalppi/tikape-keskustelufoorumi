/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
import tikape.runko.domain.Opiskelija;
import tikape.runko.domain.Viesti;

public class StatementBuilder {
    public static <K> PreparedStatement findOne(Connection connection, String table, K key) throws SQLException {
        PreparedStatement s = connection.prepareStatement("SELECT * FROM " + table + " WHERE id = ?");
        s.setObject(1, key);

        return s;
    }
    
    public static <K> PreparedStatement findAll(Connection connection, String table) throws SQLException {
        PreparedStatement s = connection.prepareStatement("SELECT * FROM " + table);
        
        return s;
    }
    
    public static <K> PreparedStatement findAllIn(Connection connection, String table, Collection<K> keys) throws SQLException {
        StringBuilder str = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            str.append(", ?");
        }
        
        PreparedStatement s = connection.prepareStatement("SELECT * FROM " + table + " WHERE id IN (" + str + ")");
        
        int i = 1;
        for(K key : keys) {
            s.setObject(i++, key);
        }
        
        return s;
    }
    
    public static Date getDate(ResultSet rs, String key) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        Date date = null;
        try {
            date = format.parse(rs.getString(key));
        } catch (ParseException e) {
            return null;
        } catch (SQLException e) {
            return null;
        }
        
        return date;
    }
}
