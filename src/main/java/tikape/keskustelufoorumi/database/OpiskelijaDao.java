/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.database;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.domain.Opiskelija;

public class OpiskelijaDao implements IDao<Opiskelija, Integer> {
    private Database database;

    public OpiskelijaDao(Database database) throws SQLException {
        this.database = database;
    }

    @Override
    public Opiskelija findOne(Integer key) {   
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = StatementBuilder.findOne(c, "Opiskelija", key);

            ResultSet rs = s.executeQuery();
            if(!rs.next()) {
                return null;
            }

            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String pwHash = rs.getString("pw_hash");

            Opiskelija o = new Opiskelija(id, nimi, pwHash);

            s.close();
            c.close();

            return o;
        } catch(SQLException e) {
            return null;
        }
    }
    
    @Override
    public Opiskelija findOneBy(String key, Object value) {
        try {
            Connection c = this.database.getConnection();
            PreparedStatement s = StatementBuilder.findOneBy(c, "Opiskelija", key, value);

            ResultSet rs = s.executeQuery();
            if(!rs.next()) {
                return null;
            }

            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String pwHash = rs.getString("pw_hash");

            Opiskelija o = new Opiskelija(id, nimi, pwHash);

            s.close();
            c.close();

            return o;
        } catch(SQLException e) {
            return null;
        }
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
            String pwHash = rs.getString("pw_hash");

            opiskelijat.add(new Opiskelija(id, nimi, pwHash));
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
            String pwHash = rs.getString("pw_hash");

            opiskelijat.add(new Opiskelija(id, nimi, pwHash));
        }
        
        s.close();
        c.close();
        
        return opiskelijat;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
    
    public void insert(String nimi, String pw) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String pwHash = Opiskelija.hashPassword(pw);
        
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.insert(c, "Opiskelija", Arrays.asList("nimi", "pw_hash"));
        
        s.setObject(1, nimi);
        s.setObject(2, pwHash);
        
        s.execute();
    }
}
