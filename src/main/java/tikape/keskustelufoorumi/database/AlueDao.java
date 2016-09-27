/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.domain.Opiskelija;

public class AlueDao implements Dao<Opiskelija, Integer> {
    private Database database;

    public AlueDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Opiskelija findOne(Integer key) throws SQLException {
        return null;
    }
    
    @Override
    public List<Opiskelija> findAll() throws SQLException {
        return null;
    }
    
    @Override
    public List<Opiskelija> findAllIn(Collection<Integer> keys) throws SQLException {
        return null;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}