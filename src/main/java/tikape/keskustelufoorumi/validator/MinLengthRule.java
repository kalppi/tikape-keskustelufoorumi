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
