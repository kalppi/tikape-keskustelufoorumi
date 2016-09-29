package tikape.keskustelufoorumi.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jarno
 */
public class PatternRule<K> implements IRule<K> {
    private Pattern pattern;
    private String reason;
    
    public PatternRule(String pattern, String reason) {
        this.pattern = Pattern.compile(pattern);
        this.reason = reason;
    }
    
    @Override
    public String getReason() {
        return this.reason;
    }
    
    @Override
    public Boolean validate(K data) {
        Matcher m = this.pattern.matcher((CharSequence)data);
        return m.matches();
    }
}
