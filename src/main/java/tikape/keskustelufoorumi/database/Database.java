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
    //private String databaseAddress;
    //private String username;
    //private String password;
    
    // rumasti tehdään näin, eikä jakseta tehdä monesta luokasta kahta eri versiota
    private Boolean isPostgres;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this(databaseAddress, "", "");
    }
    
    public Database(String databaseAddress, String username, String password) {
        //this.databaseAddress = databaseAddress;
        //this.username = username;
        //this.password = password;
        
        this.connectionPool = new BasicDataSource();
        this.connectionPool.setUsername(username);
        this.connectionPool.setPassword(password);
        this.connectionPool.setUrl(databaseAddress);
        this.connectionPool.setDriverClassName("org.postgresql.Driver");
        this.connectionPool.setInitialSize(3);
        
        System.out.println(this.connectionPool.getMaxTotal());
        
        if(databaseAddress.contains("postgres")) {
            this.isPostgres = true;
        } else {
            this.isPostgres = false;
        }
    }
    
    public Boolean isPostgres() {
        return this.isPostgres;
    }
    
    public Connection getConnection() throws SQLException {
        return this.connectionPool.getConnection();
        //return DriverManager.getConnection(this.databaseAddress, this.username, this.password);
    }

    public void init() {
        List<String> lauseet = sqliteLauseet();

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

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        
        String tyyppi = "sqlite";
        if(this.isPostgres) {
            tyyppi = "postgres";
        }
        
        lueLauseet(lista, "sql/" + tyyppi + ".sql");
        
        return lista;
    }
}
