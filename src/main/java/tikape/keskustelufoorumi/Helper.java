package tikape.keskustelufoorumi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Helper {
    public static String escapeString(String str) {
        return str.replaceAll("<", "&lt;").replaceAll(">", "&lt;");
    }

    public static LocalDateTime parseSqlDate(String dateString) {
        LocalDateTime date = null;
        date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
        return date;
    }
}
