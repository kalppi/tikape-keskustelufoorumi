/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.keskustelufoorumi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.get;
import spark.TemplateEngine;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.keskustelufoorumi.database.Dao;
import tikape.keskustelufoorumi.database.Database;
import tikape.keskustelufoorumi.database.OpiskelijaDao;
import tikape.keskustelufoorumi.domain.Alue;

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
        ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine();
        
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            
            List<Alue> alueet = new ArrayList();
            alueet.add(new Alue(1, "peruna"));
            alueet.add(new Alue(2, "tomaatti"));
            alueet.add(new Alue(3, "kurkku"));
            
            map.put("viesti", "tervehdys");
            map.put("alueet", alueet);

            return new ModelAndView(map, "index");
        }, engine);

        get("/opiskelijat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelijat", opiskelijaDao.findAll());

            return new ModelAndView(map, "opiskelijat");
        }, engine);

        get("/opiskelijat/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", opiskelijaDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "opiskelija");
        }, engine);
    }
}
