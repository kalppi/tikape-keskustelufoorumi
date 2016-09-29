package tikape.keskustelufoorumi;

public class Helper {
    public static String escapeString(String str) {
        return str.replaceAll("<", "&lt;").replaceAll(">", "&lt;");
    }
}
