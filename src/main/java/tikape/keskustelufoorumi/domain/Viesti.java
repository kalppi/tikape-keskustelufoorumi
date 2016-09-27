/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Viesti {
    private Integer id;
    private Opiskelija opiskelija;
    private Integer ketjuId;
    private LocalDateTime aika;
    private String teksti;

    public Viesti(Integer id, Opiskelija opiskelija, Integer ketjuId, LocalDateTime aika, String teksti) {
        this.id = id;
        this.opiskelija = opiskelija;
        this.ketjuId = ketjuId;
        this.aika = aika;
        this.teksti = teksti;
    }

    public Integer getId() {
        return id;
    }
    
    public void setOpiskelija(Opiskelija o) {
        this.opiskelija = o;
    }
    
    public Opiskelija getOpiskelija() {
        return this.opiskelija;
    }
    
    public String getTeksti() {
        return this.teksti;
    }
    
    public LocalDateTime getAika() {
        return this.aika;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return "[#" + this.id + " @" + this.aika.format(formatter) + " " + this.opiskelija + ": \"" + this.teksti + "\"]";
    }
}
