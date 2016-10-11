package tikape.keskustelufoorumi.database;

import java.util.Collection;
import java.util.List;

public interface IDao<T, K> {
    T findOne(K key);
    
    T findOneBy(String key, Object value);
    
    List<T> findAllBy(String key, Object value);
    
    List<T> findAllIn(Collection<K> keys);

    void delete(K key);
}
