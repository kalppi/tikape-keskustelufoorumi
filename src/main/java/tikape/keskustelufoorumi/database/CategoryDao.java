/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import tikape.keskustelufoorumi.domain.Category;
import tikape.keskustelufoorumi.domain.User;

public class CategoryDao implements IDao<Category, Integer> {
    private Database database;

    public CategoryDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Category findOne(Integer key) {
        return null;
    }
    
    @Override
    public Category findOneBy(String key, Object value) {
        return null;
    }
    
    @Override
    public List<Category> findAll() throws SQLException {
        return null;
    }
    
    @Override
    public List<Category> findAllIn(Collection<Integer> keys) throws SQLException {
        return null;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}