/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko;

import java.sql.SQLException;
import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.get;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Dao;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;

public class WebUI implements UI {
    private Database database;
    private Dao opiskelijaDao;
    
    WebUI(Database database) {
        this.database = database;
    }
    
    public void init() throws SQLException {
        this.opiskelijaDao = new OpiskelijaDao(this.database);
    }
    
    public void start() {
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelijat", opiskelijaDao.findAll());

            return new ModelAndView(map, "opiskelijat");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", opiskelijaDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "opiskelija");
        }, new ThymeleafTemplateEngine());
    }
}