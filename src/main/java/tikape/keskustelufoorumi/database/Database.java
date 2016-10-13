package tikape.keskustelufoorumi.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.dbcp2.*;

public class Database {
    private BasicDataSource connectionPool;
    
    public Database(String databaseAddress) throws ClassNotFoundException {
        this(databaseAddress, "", "");
    }
    
    public Database(String databaseAddress, String username, String password) {
        this.connectionPool = new BasicDataSource();
        this.connectionPool.setUsername(username);
        this.connectionPool.setPassword(password);
        this.connectionPool.setUrl(databaseAddress);
        this.connectionPool.setDriverClassName("org.postgresql.Driver");
        this.connectionPool.setInitialSize(3);
    }
    
    public Connection getConnection() throws SQLException {
        return this.connectionPool.getConnection();
    }

    public void init() {
        List<String> lauseet = sqlLauseet();

        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
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

    private List<String> sqlLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        lueLauseet(lista, "sql/init.sql");
        
        return lista;
    }
}
