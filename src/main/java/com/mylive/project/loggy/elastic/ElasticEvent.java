
package com.mylive.project.loggy.elastic;

import com.mylive.project.loggy.event.EEvent;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by sanjayda on 2/28/18 at 2:48 PM
 */
@Document(indexName = "loggy", type = "event ")
public class ElasticEvent
{
    @Id
    private String id;
    private Date timeStamp;
    private String threadName;
    private EEvent event;
    private String packageName;
    private String message;
    @Field(type= FieldType.Nested)
    private List<String> stackTrace = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public EEvent getEvent() {
        return event;
    }

    public void setEvent(EEvent event) {
        this.event = event;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString()
    {
        String msg = getMessage().replaceAll("'","").replaceAll("}","").replaceAll("\\{","").replaceAll("\"","");
        return "{ \"id\" :" + "\"" + getId()+"\"," +
                "\"timestamp\" :" + "\"" + getTimeStamp() + "\"," +
                "\"threadName\" :" + "\"" + getThreadName() + "\"," +
                "\"event\" :" + "\"" + getEvent().name() + "\"," +
                "\"packageName\" :" + "\"" + getPackageName() + "\"," +
                "\"message\" :" + "\"" + msg + "\"," +
                "\"stacktrace\" :" + "\"" + toStackTrace() + "\"}" ;
    }
    public String toStackTrace(){
        String str = "";
        for(String strTrace : getStackTrace()){
            str = str + strTrace + "\n";
        }
        return str;
    }
}
