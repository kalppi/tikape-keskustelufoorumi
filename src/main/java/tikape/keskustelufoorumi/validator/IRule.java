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
public interface IRule<K> {
    Boolean validate(K data);
    String getReason();
}
