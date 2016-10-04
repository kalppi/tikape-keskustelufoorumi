package tikape.keskustelufoorumi.database;

import java.sql.*;
import java.util.*;

public interface IDao<T, K> {
    T findOne(K key);
    
    T findOneBy(String key, Object value);

    List<T> findAll() throws SQLException;
    
    List<T> findAllBy(String key, Object value);
    
    List<T> findAllIn(Collection<K> keys) throws SQLException;

    void delete(K key) throws SQLException;
}
