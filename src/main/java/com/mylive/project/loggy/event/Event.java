package com.mylive.project.loggy.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sanjayda on 11/6/17 at 5:51 PM
 */

public class Event {

    private Date timeStamp;
    private String threadName;
    private EEvent event;
    private String packageName;
    private String message;
    private List<String> stackTrace = new ArrayList<>();

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getThreadName() {
        return threadName;
    }

    public EEvent getEvent() {
        return event;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setEvent(EEvent event) {
        this.event = event;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public void addStackTrace(String stackTrace) {
        this.stackTrace.add(stackTrace);
    }

    private static long longHash(String string) {
        long h = 98764321261L;
        int l = string.length();
        char[] chars = string.toCharArray();

        for (int i = 0; i < l; i++) {
            h = 31*h + chars[i];
        }
        return h;
    }

    public String getUniqueID() {
        String  sUID = getThreadName() + getEvent().name() + getMessage() + getTimeStamp() + getPackageName();
        sUID = sUID.replaceAll("[^a-zA-Z0-9]", "");
        sUID = "" + longHash(sUID);
        return sUID;
    }
}
