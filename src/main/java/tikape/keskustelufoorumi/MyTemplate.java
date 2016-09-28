/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.TemplateEngine;

/**
 *
 * @author jarno
 */
public class MyTemplate extends TemplateEngine {
    private Handlebars engine;
    private Map<String, Template> compiledTemplates = new HashMap();
    
    public MyTemplate() {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".html");
        
        this.engine = new Handlebars(loader);
        this.engine.registerHelper("url-encode", new Helper<String>() {
            public CharSequence apply(String text, Options options) {
                try {
                    return URLEncoder.encode(text, "UTF-8");
                } catch(UnsupportedEncodingException e) {
                    return text;
                }
            }
        });
    }
    
    public String render(ModelAndView modelAndView) {
        try {
            String view = modelAndView.getViewName();
            Template tpl = null;
            
            if(!this.compiledTemplates.containsKey(view)) {
                tpl = this.engine.compile(view);
                this.compiledTemplates.put(view, tpl);
            } else {
                tpl = this.compiledTemplates.get(view);
            }
            
            return tpl.apply(modelAndView.getModel());
        } catch(IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
