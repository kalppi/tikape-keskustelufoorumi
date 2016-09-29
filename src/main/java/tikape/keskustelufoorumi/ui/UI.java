package tikape.keskustelufoorumi.ui;

import java.sql.SQLException;
import tikape.keskustelufoorumi.database.Database;

public interface UI {
    void init() throws SQLException;
    void start();
}
