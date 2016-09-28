/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.ui;

/**
 *
 * @author jarno
 */
public class MenuItem {
    private Menu menu;
    private String key;
    private String text;
    private String target;
    
    public MenuItem(Menu menu, String key, String text, String target) {
        this.menu = menu;
        this.key = key;
        this.text = text;
        this.target = target;
    }
    
    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public String getTarget() {
        return target;
    }
    
    public Boolean getIsActive() {
        return this.menu.getActive().equals(this.key);
    }
}
