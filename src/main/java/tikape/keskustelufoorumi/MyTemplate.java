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
        
        this.engine.registerHelper("url", new Helper<String>() {
            public CharSequence apply(String text, Options options) {
                try {
                    StringBuilder s = new StringBuilder();
                    s.append(text);
                    for(Integer i = 0; i < options.params.length; i++) {
                        s.append(options.param(i).toString());
                    }
                    return s.toString().replaceAll(" ", "%20").toLowerCase();                
                } catch(Exception e) {
                    return text;
                }
            }
        });
        
        this.engine.registerHelper("gt", new Helper<Integer>() {
            public CharSequence apply(Integer a, Options options) {
                try {
                    Integer b = options.param(0);
                    if(a > b) {
                        return options.fn();
                    } else {
                        return options.inverse();
                    }
                } catch(Exception e) {
                    return "";
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
        
        this.engine.registerHelper("eqi", new Helper<Integer>() {
           public CharSequence apply(Integer a, Options options) {
                try {
                    Integer b = options.param(0);
                    if(a == b) {
                        return options.fn();
                    } else {
                        return options.inverse();
                    }
                } catch(Exception e) {
                    return "";
                }
            } 
        });
        
        this.engine.registerHelper("calc", new Helper<String>() {
            public CharSequence apply(String oper, Options options) {
                try {
                    Integer a = options.param(0);
                    Integer b = options.param(1);
                    Integer c = 0;
                    
                    switch(oper) {
                        case "+":
                            c = a + b;
                            break;
                        case "-":
                            c = a - b;
                            break;
                        case "*":
                            c = a * b;
                            break;
                        case "/":
                            c = a / b;
                            break;
                        case "%":
                            c = a % b;
                            break;
                    }
                    
                    return c.toString();
                } catch(Exception e) {
                    return "";
                }
            }
        });
        
        this.engine.registerHelper("times", new Helper<Integer>() {
            public CharSequence apply(Integer times, Options options) {
                try {
                    StringBuilder s = new StringBuilder();
                    for(Integer i = 1; i <= times; i++) {
                        s.append(options.fn(i));
                    }
                    return s.toString();
                } catch(IOException e) {
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
