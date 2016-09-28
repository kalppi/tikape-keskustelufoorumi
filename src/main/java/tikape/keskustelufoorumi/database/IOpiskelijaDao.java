/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.database;

import java.sql.SQLException;

/**
 *
 * @author jarno
 */
public interface IOpiskelijaDao<T, K> extends IDao<T, K> {
    void insert(String nimi, String pwHash) throws SQLException;
}
