package tikape.keskustelufoorumi.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Database {
    private String databaseAddress;
    private String username;
    private String password;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }
    
    public Database(String databaseAddress, String username, String password) {
        this.databaseAddress = databaseAddress;
        this.username = username;
        this.password = password;
    }
    
    // tähän olisi hyvä jonkinlainen connection pooli, mutta varmaan turhan overkilli nyt
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.databaseAddress, this.username, this.password);
    }

    public void init() {
        List<String> lauseet = sqliteLauseet();

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }
    
    private void lueLauseet(ArrayList<String> lista, String tiedosto) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        
        try(InputStream stream = classloader.getResourceAsStream(tiedosto)) {
            Scanner reader = new Scanner(stream);
            StringBuilder lines = new StringBuilder();
            
            while(reader.hasNextLine()) {
                String line = reader.nextLine();
                if(!line.isEmpty()) {
                    if(!line.startsWith("--")) {
                        lines.append(line);
                    }
                }
            }
            
            for(String line : lines.toString().split(";")) {
                lista.add(line);
            }
        } catch (IOException e) {
            
        }
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        
        String tyyppi = "sqlite";
        if(this.databaseAddress.contains("postgres")) {
            tyyppi = "postgres";
        }
        
        lueLauseet(lista, "sql/" + tyyppi + ".sql");
        
        return lista;
    }
}
