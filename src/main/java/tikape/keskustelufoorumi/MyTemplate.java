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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                    return URLEncoder.encode(text, "UTF-8").toLowerCase();
                } catch(UnsupportedEncodingException e) {
                    return text;
                }
            }
        });
        
        this.engine.registerHelper("eq", new Helper<String>() {
            public CharSequence apply(String text1, Options options) {
                try {
                    String text2 = options.param(0);
                    if(text1.equals(text2)) {
                        return options.fn();
                    } else {
                        return options.inverse();
                    }
                } catch(Exception e) {
                    return "";
                }
            }
        });
        
        this.engine.registerHelper("dt", new Helper<LocalDateTime>() {
            public CharSequence apply(LocalDateTime dt, Options options) {
                try {
                    if(dt == null) {
                        return "";
                    }

                    String pattern = options.param(0, "dd.MM.yyyy HH:mm:ss");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return dt.format(formatter);
                } catch(Exception e) {
                    return "";
                }
            }
        });
        
        this.engine.registerHelper("tx", new Helper<String>() {
            public CharSequence apply(String text, Options options) {
                try {
                    text = text.replace("<", "&lt;").replace(">", "&gt;");
                    
                    return new Handlebars.SafeString(tikape.keskustelufoorumi.Helper.linkify(text));
                } catch(Exception e) {
                    return "";
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
