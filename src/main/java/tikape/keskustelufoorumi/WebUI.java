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
import static spark.Spark.port;
import spark.TemplateEngine;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;
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
        
        int port = 4567;
        
        //herokua varten
        if(System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        }
        
        port(port);
        staticFileLocation("/public");
    }
    
    private Integer extractId(String text) {
        try {
            String[] parts = text.split("-");
            Integer id = Integer.parseInt(parts[0]);
            return id;
        } catch(Exception e) {
            return null;
        }
    }
    
    public void start() {
        TemplateEngine engine = new MyTemplate();
        
        get("/", (req, res) -> {            
            HashMap map = new HashMap<>();
            
            List<Alue> alueet = new ArrayList();
            alueet.add(new Alue(1, "perunakellarien iltahuvit"));
            alueet.add(new Alue(2, "tomaattien maailmanvalloitus"));
            alueet.add(new Alue(3, "kurkkusalaattien maihinnousu"));
            
            map.put("viesti", "tervehdys");
            map.put("alueet", alueet);

            return new ModelAndView(map, "index");
        }, engine);
        
        get("/alue/:id", (req, res) -> {
            return extractId(req.params(":id"));
        });

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
