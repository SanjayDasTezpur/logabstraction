package com.mylive.project.loggy.util;

import com.mylive.project.loggy.client.Client;
import com.mylive.project.loggy.client.ClientsRepository;
import com.mylive.project.loggy.client.TimeLine;
import com.mylive.project.loggy.event.Event;
import com.mylive.project.loggy.mailGenerator.LoggyReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sanjayda on 2/9/18 at 4:24 PM
 */

public class LoggyTrackerHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoggyReportGenerator.class);

    public static void createTableIfNotExist(JdbcTemplate jt, String sTableName) {
        String sQuery = "create table if not exists "+sTableName+ " (u_id VARCHAR(255) not null, thread_name VARCHAR(255), package_name TEXT, message TEXT, time_stamp TIMESTAMP, stack_trace TEXT, html_line LONGTEXT, type VARCHAR(100), primary key (u_id))";
        jt.execute(sQuery);
    }

    public static void logEvent(JdbcTemplate jt, Event event, String sTableName) {
        if(event.getEvent().name().equalsIgnoreCase("INFO"))
        {
            return;
        }
        String sQuery="";
        try {
            String sHTMLLine = LoggyWebUtil.appendToHtml(event);
            String stacktrace = "";
            String sUID = event.getUniqueID();
            for (String trace : event.getStackTrace()) {
                stacktrace += "<div>" + trace + "</div>";
            }
            sQuery = "INSERT INTO " + sTableName +
                    " VALUES ('" + sUID + "', '" + event.getThreadName() + "', '" + event.getPackageName() + "', '" +
                    event.getMessage() + "', '" + new Timestamp(event.getTimeStamp().getTime()) + "', '" +
                    stacktrace + "', '" + sHTMLLine + "', '" + event.getEvent() + "')";

            jt.execute(sQuery);
        } catch (DuplicateKeyException e) {
            //logger.info("Got SQL Duplicate Entry Exception, Ignoring exception \n"+sQuery);
        } catch (Exception e) {
            logger.error("Got SQL Exception : " + e.getMessage());
        }

    }

    public static void connectClient(ClientsRepository repo, String sHostName, String sProductName) {

        String id = sHostName + "_" + sProductName;
        Client client = repo.findById(id);
        if(null == client) {
            client = new Client();
            client.setCreated_date(new Date());
            client.setLast_connection(new Date());
            client.setId(id);
            client.setName(sProductName);
            client.setIs_connected(1);
            client.setServer(sHostName);
        } else {
            client.setLast_connection(new Date());
            client.setIs_connected(1);
        }
        repo.save(client);
    }

    public static void disconnectClient(ClientsRepository repo, String sHostName, String sProductName) {
        String id = sHostName + "_" + sProductName;
        Client client = repo.findById(id);
        if(null != client) {
            client.setIs_connected(0);
            repo.save(client);
        }
    }

    public static List<Map<String, Object>> collectQueryStat(JdbcTemplate jt, String hostname, String productname) {
        String table = hostname + "_" + productname;
        return jt.queryForList("select count(type) as count,type from `" + table + "` group by type \n");
    }

    public static  List<Map<String, Object>> collectTrendStat( JdbcTemplate jt, String hostname, String productname) {
        String table = hostname + "_" + productname;
        return jt.queryForList("select count(type) as count,type from `" + table + "` group by type \n");
    }

    public static List<Map<String, Object>> collectError(JdbcTemplate jt, String hostname, String productname, String sType) {
        String table = hostname + "_" + productname;
        return jt.queryForList("select * from `" + table + "` where type='" + sType + "'");
    }

    public static List<Map<String, Object>> collectSpecificInfo(JdbcTemplate jt, String hostname, String product, TimeLine dt) {
        String table = hostname + "_" + product;
        List<Map<String, Object>> infoResult = jt.queryForList("select * from `" + table + "` where type = 'info' AND time_stamp > '" + dt.getsFrom() + "' AND time_stamp < '" + dt.getsTo() + "' \n");
        return infoResult;
    }
}
