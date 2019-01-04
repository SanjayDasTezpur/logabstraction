package com.mylive.project.loggy.controller;

import com.mylive.project.loggy.client.Client;
import com.mylive.project.loggy.client.ClientsRepository;
import com.mylive.project.loggy.client.SubscriberDTO;
import com.mylive.project.loggy.client.TimeLine;
import com.mylive.project.loggy.util.LoggerDBRetention;
import com.mylive.project.loggy.util.LoggyTrackerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class LoggyController {

    private JdbcTemplate jdbcTemplate;


    @Autowired
    ClientsRepository clientsRepository;

    @Autowired
    public LoggyController(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping("/clients")
    public List<Client> getClients() {
        return (List<Client>) clientsRepository.findAll();
    }

    @RequestMapping("/stat/{hostname}/{product}")
    public List<Map<String, Object>> getStat(@PathVariable("hostname") String hostname, @PathVariable("product") String product) {
        return LoggyTrackerHandler.collectQueryStat(jdbcTemplate, hostname, product);
    }

    @RequestMapping("/trend/{hostname}/{product}")
    public List<Map<String, Object>> getTrendStat(@PathVariable("hostname") String hostname, @PathVariable("product") String product) {
        return LoggyTrackerHandler.collectTrendStat(jdbcTemplate, hostname, product);
    }

    @RequestMapping(value = "/subscriber/{hostname}/{product}", method = RequestMethod.POST)
    public String addSubscriber(@PathVariable("hostname") String hostname, @PathVariable("product") String product, @RequestBody SubscriberDTO sub) {
        Client cleint = clientsRepository.findById(hostname + "_" + product);
        if (null != cleint && null != sub.getEmail()) {
            cleint.addErrorSubscriber(sub.getEmail());
            clientsRepository.save(cleint);
            return "Added successfully";
        }
        return "Unable to add subscriber";
    }
/*
    @RequestMapping(value = "/subscriber/{hostname}/{product}", method = RequestMethod.POST)
    public String removeSubscriber(@PathVariable("hostname") String hostname, @PathVariable("product") String product, @RequestBody SubscriberDTO sub) {
        Client cleint = clientsRepository.findById(hostname + "_" + product + ".log");
        if (null != cleint && null != sub.getEmail()) {
            cleint.addErrorSubscriber(sub.getEmail());
            clientsRepository.save(cleint);
            return "Added successfully";
        }
        return "Unable to add subscriber";
    }*/

    @RequestMapping(value = "/event/{hostname}/{product}/{type}", method = RequestMethod.GET)
    public List<Map<String, Object>> getError(@PathVariable("hostname") String hostname, @PathVariable("product") String product, @PathVariable("type") String type) {
        return  LoggyTrackerHandler.collectError(jdbcTemplate, hostname, product, type);
    }
    @RequestMapping(value = "/specificinfo/{hostname}/{product}", method = RequestMethod.POST)
    public List<Map<String, Object>> getSpecificInfo(@PathVariable("hostname") String hostname, @PathVariable("product") String product, @RequestBody TimeLine dt) {
        return  LoggyTrackerHandler.collectSpecificInfo(jdbcTemplate, hostname, product, dt);
    }

    @RequestMapping(value = "/dbretention/{hostname}/{product}/start", method = RequestMethod.GET)
    public String setDBRetentionTime(@PathVariable("hostname") String hostname, @PathVariable("product") String product) {
        LoggerDBRetention.cleanDB(jdbcTemplate, hostname, product);
        return "Event INFO will be cleaned after an interval of 12 hours for the machine " + hostname + "and product " + product;
    }
}
