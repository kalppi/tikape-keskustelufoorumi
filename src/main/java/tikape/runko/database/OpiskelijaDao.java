/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import tikape.runko.domain.Opiskelija;

public class OpiskelijaDao implements Dao<Opiskelija, Integer> {
    private Database database;

    public OpiskelijaDao(Database database) throws SQLException {
        this.database = database;
    }

    @Override
    public Opiskelija findOne(Integer key) throws SQLException {   
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.findOne(c, "Opiskelija", key);

        ResultSet rs = s.executeQuery();
        if(!rs.next()) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Opiskelija o = new Opiskelija(id, nimi);

        s.close();
        c.close();

        return o;
    }

    @Override
    public List<Opiskelija> findAll() throws SQLException {
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.findAll(c, "Opiskelija");

        ResultSet rs = s.executeQuery();
        List<Opiskelija> opiskelijat = new ArrayList<>();
        while(rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            opiskelijat.add(new Opiskelija(id, nimi));
        }
        
        s.close();
        c.close();

        return opiskelijat;
    }
    
    @Override
    public List<Opiskelija> findAllIn(Collection<Integer> keys) throws SQLException {
        if(keys.isEmpty()) {
            return new ArrayList();
        }
        
        List<Opiskelija> opiskelijat = new ArrayList();
        
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.findAllIn(c, "Opiskelija", keys);
        ResultSet rs = s.executeQuery();
        
        if(rs == null) {
            return new ArrayList();
        }
        
        while(rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            opiskelijat.add(new Opiskelija(id, nimi));
        }
        
        s.close();
        c.close();
        
        return opiskelijat;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}
