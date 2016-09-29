package tikape.keskustelufoorumi.validator;

/**
 *
 * @author jarno
 */
public class MinLengthRule<K> implements IRule<K> {
    private Integer min;
    private String reason;
    
    public MinLengthRule(Integer min, String reason) {
        this.min = min;
        this.reason = reason;
    }
    
    @Override
    public String getReason() {
        return this.reason;
    }
    
    @Override
    public Boolean validate(K data) {
        return ((String)data).length() >= this.min;
    }
}
