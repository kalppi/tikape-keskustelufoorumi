/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jarno
 */
public class Menu {
    private Map<String, MenuItem> items = new LinkedHashMap();
    private String active = null;
    
    private void addItem(MenuItem item) {
        this.items.put(item.getKey(), item);
        
        if(this.active == null) {
            this.active = item.getKey();
        }
    }
    
    public void addItem(String key, String text, String target) {
        this.addItem(new MenuItem(this, key, text, target));
    }
    
    public Collection<MenuItem> getItems() {
        return this.items.values();
    }
    
    public void setActive(String key) {
        this.active = key;
    }
    
    public MenuItem getActive() {
        return this.items.get(this.active);
    }
}
