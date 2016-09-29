package tikape.keskustelufoorumi.validator;

/**
 *
 * @author jarno
 */
public interface IRule<K> {
    Boolean validate(K data);
    String getReason();
}
