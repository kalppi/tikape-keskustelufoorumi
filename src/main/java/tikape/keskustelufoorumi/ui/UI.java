/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.ui;

import java.sql.SQLException;
import tikape.keskustelufoorumi.database.Database;

public interface UI {
    void init() throws SQLException;
    void start();
}
