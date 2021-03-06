package tikape.keskustelufoorumi.ui;

import java.util.function.Function;
import tikape.keskustelufoorumi.Context;

/**
 *
 * @author jarno
 */
public class MenuItem {
    private Menu menu;
    private String key;
    private String text;
    private String target;
    private Function<Context, Boolean> show;
    
    public MenuItem(Menu menu, String key, String text, String target) {
        this(menu, key, text, target, null);
    }
    
    public MenuItem(Menu menu, String key, String text, String target, Function<Context, Boolean> show) {
        this.menu = menu;
        this.key = key;
        this.text = text;
        this.target = target;
        this.show = show;
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
    
    public Function<Context, Boolean> getShowFunction() {
        return this.show;
    }
}
