/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author jarno
 */
public class Viesti {
    private Integer id;
    private Opiskelija opiskelija;
    private Integer ketjuId;
    private Date aika;
    private String teksti;

    public Viesti(Integer id, Opiskelija opiskelija, Integer ketjuId, Date aika, String teksti) {
        this.id = id;
        this.opiskelija = opiskelija;
        this.ketjuId = ketjuId;
        this.aika = aika;
        this.teksti = teksti;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setOpiskelija(Opiskelija o) {
        this.opiskelija = o;
    }
    
    public Opiskelija getOpiskelija() {
        return this.opiskelija;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dt.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
        return "[#" + this.id + " @" + dt.format(this.aika) + " " + this.opiskelija + ": \"" + this.teksti + "\"]";
    }
}
