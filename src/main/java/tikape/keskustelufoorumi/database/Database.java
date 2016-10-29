package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.*;

public class Database {
    private BasicDataSource connectionPool;
    
    public Database(String databaseAddress) {
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
}
