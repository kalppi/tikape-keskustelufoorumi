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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import tikape.runko.domain.Opiskelija;
import tikape.runko.domain.Viesti;
import tikape.runko.database.StatementBuilder;


public class ViestiDao implements Dao<Viesti, Integer> {
    private Database database;
    private OpiskelijaDao opiskelijaDao;

    public ViestiDao(Database database) throws SQLException {
        this.database = database;
        this.opiskelijaDao = new OpiskelijaDao(this.database);
    }
    
    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.findOne(c, "Viesti", key);
        ResultSet rs = s.executeQuery();
        
        if(!rs.next()) {
            return null;
        }
        
        Integer id = rs.getInt("id");
        Integer opiskelijaId = rs.getInt("opiskelija_id");
        Integer ketjuId = rs.getInt("ketju_id");
        String teksti = rs.getString("teksti");
        Date aika = StatementBuilder.getDate(rs, "aika");
        
        s.close();
        c.close();
        
        Opiskelija opiskelija = this.opiskelijaDao.findOne(opiskelijaId);
        
        return new Viesti(id, opiskelija, ketjuId, aika, teksti);
    }
    
    @Override
    public List<Viesti> findAll() throws SQLException {
        return null;
    }
    
    @Override
    public List<Viesti> findAllIn(Collection<Integer> keys) throws SQLException {
        if(keys.isEmpty()) {
            return new ArrayList();
        }
        
        List<Viesti> viestit = new ArrayList();
        
        Connection c = this.database.getConnection();
        PreparedStatement s = StatementBuilder.findAllIn(c, "Viesti", keys);
        ResultSet rs = s.executeQuery();
        
        if(rs == null) {
            return new ArrayList();
        }
        
        Map<Integer, List<Viesti>> opiskelijaMap = new HashMap();
        
        while(rs.next()) {
            Integer id = rs.getInt("id");
            Integer opiskelijaId = rs.getInt("opiskelija_id");
            Integer ketjuId = rs.getInt("ketju_id");
            String teksti = rs.getString("teksti");
            Date aika = StatementBuilder.getDate(rs, "aika");
                        
            Viesti viesti = new Viesti(id, null, ketjuId, aika, teksti);
            viestit.add(viesti);
            
            if(!opiskelijaMap.containsKey(opiskelijaId)) {
                opiskelijaMap.put(opiskelijaId, new ArrayList());
            }
            
            opiskelijaMap.get(opiskelijaId).add(viesti);
        }

        List<Opiskelija> opiskelijat = this.opiskelijaDao.findAllIn(opiskelijaMap.keySet());
        
        for(Opiskelija o : opiskelijat) {
            for(Viesti v : opiskelijaMap.get(o.getId())) {
                v.setOpiskelija(o);
            }
        }
        
        s.close();
        c.close();
        
        return viestit;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}
