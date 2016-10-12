package tikape.keskustelufoorumi.database;

import java.util.List;

/**
 *
 * @author jarno
 */
public interface IPageableDao<T> {
    List<T> findAll(Integer start, Integer limit);
    List<T> findAllBy(String key, Object value, Integer start, Integer limit);
}
