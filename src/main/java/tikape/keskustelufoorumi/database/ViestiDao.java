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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import tikape.keskustelufoorumi.domain.Opiskelija;
import tikape.keskustelufoorumi.domain.Viesti;
import tikape.keskustelufoorumi.database.StatementBuilder;


public class ViestiDao implements IDao<Viesti, Integer> {
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
        LocalDateTime aika = StatementBuilder.getDate(rs, "aika");
        
        s.close();
        c.close();
        
        Opiskelija opiskelija = this.opiskelijaDao.findOne(opiskelijaId);
        
        return new Viesti(id, opiskelija, ketjuId, aika, teksti);
    }
    
    @Override
    public Viesti findOneBy(String key, Object value) throws SQLException {
        return null;
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
        
        List<String> fields =  Arrays.asList("id", "opiskelija_id", "ketju_id", "teksti");
        
        if(this.database.isPostgres()) {
            fields.add("aika AT TIME ZONE 'Europe/Helsinki'");
        } else {
            fields.add("DATETIME(aika, 'localtime') AS aika");
        }
        
        PreparedStatement s = StatementBuilder.findAllIn(c, "Viesti", keys, fields);
        
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
            LocalDateTime aika = StatementBuilder.getDate(rs, "aika");
                        
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
