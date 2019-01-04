package com.mylive.project.loggy.youtrack;

import com.mylive.project.loggy.components.YTProjectMap;
import com.mylive.project.loggy.elastic.ElasticEvent;
import com.mylive.project.loggy.elastic.ElasticEventOperations;
import com.mylive.project.loggy.event.Event;
import com.mylive.project.loggy.utilservice.RestServiceComponent;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class YouTrackFileBug {
    private static final Logger logger = LoggerFactory.getLogger(YouTrackFileBug.class);

    @Autowired
    YoutrackConfiguration  yConf;

    @Autowired
    RestServiceComponent restServiceComponent;

    @Autowired
    YTProjectMap map;

    @Autowired
    ElasticEventOperations eso;


    public void  fileBugOnYoutrack(Event event, String sProduct, String sHost)
    {

        if(event.getEvent().name().equalsIgnoreCase("ERROR") && !event.getStackTrace().isEmpty() )
        {
            ElasticEvent elasticEvent = eso.makeElasticEvent(event);
            if(decideToFileBug(elasticEvent.toStackTrace() + elasticEvent.getMessage()) && decideToFileBug(elasticEvent)) {
                String url = makeYTBugFileAPI(sProduct, elasticEvent.getMessage(), elasticEvent.toStackTrace(),sHost);
                if(null == url)
                    return;
                Response response = restServiceComponent.runPutWithEmptyBOdy(url);
                String[] locations = response.header("Location").split("/");
                String newBugID = locations[locations.length-1];
                updateBug(newBugID);
               // map.getBugMap().put(makeHash(elasticEvent),locations[locations.length-1]);
                map.registerBug(makeHash(elasticEvent),newBugID);
            }
            else {
                logger.info("Error/Exception caught but not auto filed in youtrack, because Loggy decided not to file bug");
            }
        }
    }

    private String makeYTBugFileAPI(String sProject, String summary, String description, String host){
        if(null == map.getProjectMap().get(sProject)){
            logger.info("Project not configured in loggy to file bug in youtrack");
            return null;
        }
        summary = "Exception found, Message - " + summary ;
        description = "Found in host " + host + "\n" + description;
        String sApi = "/rest/issue?project=";
        String youtrackEndPoint = yConf.getYoutrackEndPoint();
        String sSummary = "&summary=" + summary.replaceAll(" ","+") + "&";
        String sDescription = "description=" + description.replaceAll(" ","+");
        String finalURL = youtrackEndPoint+sApi + map.getProjectMap().get(sProject)+sSummary+sDescription;
        return finalURL;
    }
    private void updateBug(String BugID){
        String youtrackEndPoint = yConf.getYoutrackEndPoint();
        String sApi = "/rest/issue/" + BugID + "/execute";
        String finalURL = youtrackEndPoint + sApi;
        String toBugCommand = "command=Subsystem%20backend%20Found%20In%20Integration%20Enviroment%20Found%20in%20Version%20None";
        Response response = restServiceComponent.runPost(finalURL, toBugCommand);
        restServiceComponent.runPost(finalURL,"command=Type%20Bug");
        try {
            if(response.isSuccessful())
                logger.info("Issue Updated to Bug " + response.body().string());
            else
                logger.error("Issue Update to Bug is failed " + response.body().string());
        } catch (IOException e){
            logger.error("Issue Update to Bug is failed  (IOException caught)" + e.getMessage());
        }
    }

    private boolean decideToFileBug(String stack){
        boolean bVal = false;
        if(stack.contains("AlarmClock task is late"))
        {
            bVal = false;
            logger.info("The caught Exception \"AlarmClock task is late\"");
            return bVal;
        }
        bVal = ExceptionChecker.checkAllowedException(stack);
        if(bVal == false){
            logger.info("The caught Exception is not listed in ExceptionChecker List");
        }

        return bVal;
    }

    private boolean decideToFileBug(ElasticEvent eevent){
        String hash = makeHash(eevent);
        if(map.getBugMap().isEmpty())
        {
            return true;
        }
        String bugID = map.getBugMap().containsKey(hash) ? map.getBugMap().get(hash) : null;
        if( null == bugID){
            return true;
        }
        String finalUrl = yConf.getYoutrackEndPoint()+"/rest/issue/" + bugID;
        Response response = restServiceComponent.runGet(finalUrl);
        if(response.code()!= 200)
        {
            logger.error("Bug Search in Youtrack is not successfull, Code :- "+response.code());
            return true;
        }
        else {
            logger.info("Bug Search in Youtrack is successfull, Code :- "+response.code());
        }
        String xml = resultResponsBody(response);
        String state = findStateOfBug(xml);

        if (state.equalsIgnoreCase("Fixed"))
        {
            return true;
        }
        return false;
    }

    private String makeHash(ElasticEvent eevent) {
        String javaFilenames ="";
        String msg = eevent.getMessage();
        char[] chars = msg.toCharArray();
        String newID = "";
        for(char c : chars)
        {
            if(c>64 && c<91)
                newID = newID + c;
            if(c>96 && c<123)
                newID = newID + c;
        }

        return newID;
    }

    private String resultResponsBody(Response response )
    {
        String xml = null;
        try {
            xml = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Got IOException In getting Response In string"+e.getMessage());
        }
        return xml;
    }

    private String findStateOfBug(String xml)
    {
        String state = "";
        try {
            JSONObject jsonObject = XML.toJSONObject(xml);
            JSONObject issue = (JSONObject) jsonObject.get("issue");
            JSONArray field = (JSONArray) issue.get("field");
            for (Object attr : field)
            {
                String name = (String) ((JSONObject) attr).get("name");
                if(name.equalsIgnoreCase("State"))
                {
                    state = (String) ((JSONObject) attr).get("value");
                }

            }
        } catch (JSONException e){
            logger.error("JSONException found "+e.getMessage());
        } catch (Exception e){
            logger.error("Exception found "+e.getMessage());
        }
        return state;
    }


}
