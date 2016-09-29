package tikape.keskustelufoorumi.validator;

/**
 *
 * @author jarno
 */
public class MaxLengthRule<K> implements IRule<K> {
    private Integer max;
    private String reason;
    
    public MaxLengthRule(Integer max, String reason) {
        this.max = max;
        this.reason = reason;
    }
    
    @Override
    public String getReason() {
        return this.reason;
    }
    
    @Override
    public Boolean validate(K data) {
        return ((String)data).length() <= this.max;
    }
}
