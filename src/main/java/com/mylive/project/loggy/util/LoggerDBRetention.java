package com.mylive.project.loggy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by sanjayda on 2/21/18 at 3:00 PM
 */

public class LoggerDBRetention implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(LoggerDBRetention.class);
    private static Map<String,Thread> lstThread = new HashMap();
    final static int interval=6;
    String sTableName;
    JdbcTemplate jt;


    public int getInterval() {
        return interval;
    }

    public String getsTableName() {
        return sTableName;
    }

    public void setsTableName(String sTableName) {
        this.sTableName = sTableName;
    }

    public JdbcTemplate getJt() {
        return jt;
    }

    public void setJt(JdbcTemplate jt) {
        this.jt = jt;
    }

    public static Map<String, Thread> getLstThread() {
        return lstThread;
    }

    public static void setLstThread(Map<String, Thread>lstThread) {
        LoggerDBRetention.lstThread = lstThread;
    }

    @Override
    public void run()
    {
        logger.info("Cleaning service is started and interval set to "+interval+" hours for event INFO");
        List<Map<String, Object>> timeMap = jt.queryForList("select MAX(time_stamp) as maxTime from " + getsTableName());
        Timestamp maxTime = (Timestamp) timeMap.get(0).get("maxTime");
        while( true ) {
            _sleep(60*60*interval);
            String sQuery = "delete from "+this.getsTableName()+" where time_stamp <= '"+maxTime+"' and type='INFO'";
            this.jt.execute(sQuery);
            logger.info("All INFO data before " + maxTime + " are cleared ");
            sQuery = "delete from "+this.getsTableName()+" where time_stamp <= '"+maxTime+"' and type='WARN'";
            this.jt.execute(sQuery);
            timeMap = jt.queryForList("select MAX(time_stamp) as maxTime from " + getsTableName());
            logger.info("All WARN data before " + maxTime + " are cleared ");
            maxTime = (Timestamp) timeMap.get(0).get("maxTime");
        }
    }

    public static void cleanDB(JdbcTemplate jt, String sHost, String sProduct)
    {
        LoggerDBRetention lr = new LoggerDBRetention();
        lr.setJt(jt);
        lr.setsTableName(sHost+"_"+sProduct);
        Thread cleanThred = new Thread( lr );
        cleanThred.start();

    }
    public static void cleanDBAtInterval(JdbcTemplate jt, String sHost, String sProduct)
    {
        //user can give their own retention time , yet to be implemented

        LoggerDBRetention lr = new LoggerDBRetention();
        lr.setJt(jt);
        lr.setsTableName(sHost+"_"+sProduct);
        Thread cleanThred = new Thread( lr );
        cleanThred.start();

    }
    private void _sleep(int inSecond) {
        try {
            sleep(1000 * inSecond);
        } catch (InterruptedException e) {
            logger.error("Got Exception :" + e.getMessage());
        }
    }
}
