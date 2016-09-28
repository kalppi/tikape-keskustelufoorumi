/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
