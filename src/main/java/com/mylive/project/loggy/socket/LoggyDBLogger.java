package com.mylive.project.loggy.socket;

import com.google.gson.Gson;
import com.mylive.project.loggy.client.AgentSubscriber;
import com.mylive.project.loggy.client.ClientsRepository;
import com.mylive.project.loggy.elastic.ElasticEventOperations;
import com.mylive.project.loggy.event.Event;
import com.mylive.project.loggy.mailGenerator.MailHandler;
import com.mylive.project.loggy.parsing.LoggyParser;
import com.mylive.project.loggy.util.LoggerDBRetention;
import com.mylive.project.loggy.util.LoggyTrackerHandler;
import com.mylive.project.loggy.youtrack.YouTrackFileBug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;
/*
 * Created by sanjayda on 2/6/18 at 2:13 PM
 */


public class LoggyDBLogger implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LoggyDBLogger.class);
    private static final String DEFAUT_VAL_WHEN_JSON_FAILED = "----PARSED ERROR IN LOGGY----";
    private final ClientsRepository clientsRepo;
    private Socket socket;
    private BufferedReader is = null;
    private PrintWriter os = null;
    private String sClientHostName;
    private String sTableName;
    private Gson gson = new Gson();
    private YouTrackFileBug ytb;
    JdbcTemplate jt;


    ElasticEventOperations eso ;


    public LoggyDBLogger(Socket socket, JdbcTemplate jt, ClientsRepository clientsRepository, ElasticEventOperations eop, YouTrackFileBug ytb) {
        this.socket = socket;
        this.jt = jt;
        this.clientsRepo = clientsRepository;
        sClientHostName = socket.getInetAddress().getHostName();
        this.eso= eop;
        this.ytb = ytb;
    }



    @Override
    public void run() {
        try {
            os = new PrintWriter(socket.getOutputStream());
            os.write("Server listed this agent for logging\n");
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            logger.error("Got Exception :" + e.getMessage());
            return;
        }


        String sLine = getLine();
        AgentSubscriber as  = getAgenSuscriberFromJSON(sLine);
        if(null != as) {
            String sProductName = as.getApp();
            try {
                if (null != sProductName) {
                    sTableName = "`" + as.getHost() + "_" + sProductName + "`";
                    LoggyTrackerHandler.createTableIfNotExist(jt, sTableName);
                    LoggyTrackerHandler.connectClient(clientsRepo, sClientHostName, sProductName);
                    LoggerDBRetention.cleanDB(jt, as.getHost(), sProductName);

                    // sTableName is used as Elastic Index name ( Which should be prefereable)
                    eso.createElasticIndex(sTableName,"default");

                } else {
                    logger.warn("Product name is not found will return from logger");
                    return;
                }

            } catch (Exception e) {
                logger.error("Got exception while connecting client: " + e.getMessage());
                return;
            }
            String line = as.getMessage();
            while (!os.checkError()) {
                Event event = LoggyParser.parse(line);
                line = getParsedLineFromAgent(getLine());
                while (!LoggyParser.isEvent(line)) {
                    event.getStackTrace().add(line);
                    line = getParsedLineFromAgent(getLine());
                }
                LoggyTrackerHandler.logEvent(jt, event, sTableName);
                eso.saveBulkElasticEvent(sTableName,"default",event);
                MailHandler.sendMail(event, clientsRepo, sClientHostName + "_" + sProductName);
                ytb.fileBugOnYoutrack(event,sProductName,sClientHostName);
                os.println();

            }


            try {
                logger.info("Connection Closing..");
                if (is != null) {
                    is.close();
                    logger.info("Socket in stream closed for : " + sClientHostName + "_" + sProductName);
                }
                if (os != null) {
                    os.close();
                    logger.info("Socket out stream closed for : " + sClientHostName + "_" + sProductName);
                }
                LoggyTrackerHandler.disconnectClient(clientsRepo, sClientHostName, sProductName);
                logger.info("Connection Closed");
           /* if (socket != null) {
                socket.close();
                logger.info("Socket Closed");
            }*/
            } catch (Exception ie) {
                logger.warn("Client listner closing error : " + ie.getMessage());
            }
        }
    }

    private AgentSubscriber getAgenSuscriberFromJSON(String sLine) {
        AgentSubscriber as=null;
        try {
            String str = sLine.replaceAll("\\\\", "");
            as = gson.fromJson(str, AgentSubscriber.class);
        } catch (Exception e){
            logger.warn("Parsing Exception of JSON in - getAgenSuscriberFromJSON() Trrying to Fix. :- " + sLine);
            String[] split = sLine.split("\"message\":");
            String strGreet = split[0] + "\"message\":" + "\"" + DEFAUT_VAL_WHEN_JSON_FAILED + "\""+"}";
            as = gson.fromJson(strGreet, AgentSubscriber.class);
        }
        return as;
    }

    private void _sleep() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Got Exception :" + e.getMessage());
        }
    }

    private String getLine() {
        String logLine = "";
        try {
            logLine = is.readLine();
            if(null != logLine) {
                return logLine;
            }
            else {
                 throw new IOException();
            }
        } catch (IOException e) {
            logger.error("Got Exception :" + e.getMessage());
        }
        return logLine;
    }
    public String getParsedLineFromAgent(String rawLine){
        String[] split = rawLine.split("\"message\":");
        String string = split[split.length - 1].replaceAll("\"", " ");
        string = string.replaceAll("}", "");
        string = string.replaceAll("'", "");

        rawLine = split[0] + "\"message\":" + "\"" + string + "\""+"}";
        String ret;
        try {
            ret =   getAgenSuscriberFromJSON(rawLine).getMessage();
        } catch (Exception e){
            ret =   getAgenSuscriberFromJSON(rawLine).getMessage();
            logger.error("Parsing Exception of JSON :- "+rawLine);
        }
        return ret;
    }


}
