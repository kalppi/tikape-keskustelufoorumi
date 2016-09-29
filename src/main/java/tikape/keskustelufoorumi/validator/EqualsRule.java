package tikape.keskustelufoorumi.validator;

/**
 *
 * @author jarno
 */
public class EqualsRule<K> implements IRule<K> {
    private K value;
    private String reason;
    
    public EqualsRule(K value, String reason) {
        this.value = value;
        this.reason = reason;
    }
    
    @Override
    public String getReason() {
        return this.reason;
    }
    
    @Override
    public Boolean validate(K data) {
        return this.value.equals(data);
    }
}
