package com.mylive.project.loggy.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class YTProjectMap {
    private static final Logger logger = LoggerFactory.getLogger(YTProjectMap.class);

    Map<String, String> projectMap = new HashMap<>();
    Map<String, String> BugMap = new HashMap<>();

    @Autowired
    JdbcTemplate jt;

    public YTProjectMap() {
        /* key is product name which is running and Value is Product ID configured in youtrack */
        projectMap.put("youtrack","YOUTRACK");
        projectMap.put("intelij","INTELIJ");
        projectMap.put("pycharm","PYCHARM");
        projectMap.put("teamcity","TEAMCITY");
    }

    @Bean
    private String  loadBug() {
        String sQuery = "create table if not exists youtrack_bug (hash_id VARCHAR(255) not null, bug_id VARCHAR(255))";
        jt.execute(sQuery);
        List<Map<String, Object>> queryForList = jt.queryForList("select * from youtrack_bug");
        for(Map<String, Object> mp : queryForList ){
            BugMap.put((String) mp.get("hash_id"), (String) mp.get("bug_id"));
        }
        logger.info(queryForList.size() + " No of Bug Loaded from DB");
        return "";
    }

    public Map<String, String> getProjectMap() {
        return projectMap;
    }

    public void setProjectMap(Map<String, String> projectMap) {
        this.projectMap = projectMap;
    }

    public Map<String, String> getBugMap() {
        return BugMap;
    }

    public void setBugMap(Map<String, String> bugMap) {
        BugMap = bugMap;
    }

    public void registerBug(String hash_id, String bug_id)
    {
        getBugMap().put(hash_id,bug_id);
        String sQuery = "INSERT INTO youtrack_bug VALUES ( '" + hash_id + "' , '" + bug_id + "')";
        jt.execute(sQuery);
    }
}
