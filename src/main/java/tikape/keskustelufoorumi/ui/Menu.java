/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.ui;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jarno
 */
public class Menu {
    private List<MenuItem> items = new ArrayList();
    private String active = null;
    
    private void addItem(MenuItem item) {
        this.items.add(item);
        
        if(this.active == null) {
            this.active = item.getKey();
        }
    }
    
    public void addItem(String key, String text, String target) {
        this.addItem(new MenuItem(this, key, text, target));
    }
    
    public List<MenuItem> getItems() {
        return this.items;
    }
    
    public void setActive(String key) {
        this.active = key;
    }
    
    public String getActive() {
        return this.active;
    }
}
