package tikape.keskustelufoorumi.database;

import java.util.List;

/**
 *
 * @author jarno
 */
public interface IPageableDao<T> {
    List<T> findAll(Integer start, Integer limit);
}
