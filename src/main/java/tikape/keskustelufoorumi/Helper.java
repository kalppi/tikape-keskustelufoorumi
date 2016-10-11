package tikape.keskustelufoorumi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static String escapeString(String str) {
        return str.replaceAll("<", "&lt;").replaceAll(">", "&lt;");
    }

    public static LocalDateTime parseSqlDate(String dateString) {
        LocalDateTime date = null;
        date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
        return date;
    }
    
    private static Pattern linkPattern = null;
    
    public static String linkify(String text) {
        if(Helper.linkPattern == null) {
            Helper.linkPattern = Pattern.compile("(?:(https?://)|(?:www\\.))[^\\s]+");
        }
        
        Matcher m = Helper.linkPattern.matcher(text);
        
        StringBuffer result = new StringBuffer();

        while(m.find()) {
            MatchResult r = m.toMatchResult();
            
            String url = m.group(0);
            if(m.group(1) == null) {
                url = "http://" + url;
            }
            
            url = "<a href=\"" + url + "\">" + m.group(0) + "</a>";
            
            m.appendReplacement(result, url);
        }
        
        m.appendTail(result);
        
        return result.toString();
    }
}

