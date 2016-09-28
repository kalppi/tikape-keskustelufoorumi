/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.validator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jarno
 */
public class Validator<K> implements IRule<K> {
    private List<IRule> rules = new ArrayList();
    private String reason;
    
    public Validator() {
        
    }
        
    public String getReason() {
        return this.reason;
    }
    
    public void addRule(IRule<K> rule) {
        this.rules.add(rule);
    }
    
    public Boolean validate(K data) {
        for(int i = 0; i < this.rules.size(); i++) {
            IRule rule = this.rules.get(i);
            
            if(rule.validate(data) == false) {
                this.reason = rule.getReason();
                return false;
            }
        }
        
        return true;
    }
}
