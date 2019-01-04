package com.mylive.project.loggy.util;

import com.mylive.project.loggy.event.Event;

/**
 * Created by sanjayda on 11/13/17 at 12:34 PM
 */

public class LoggyWebUtil {
    public static String appendToHtml(Event event) {

        String totalTrace="";
        String color = event.getEvent().getColor();
        String stacktrace = "";

        for(String trace : event.getStackTrace()){
            stacktrace += "<div>" + trace + "</div>";
            totalTrace = totalTrace+trace;
        }

        stacktrace = "<span style=\"color:"+color+"\">" + stacktrace + "</span>";

        String sPrintableEvent = "<div>" + event.getTimeStamp().toString() + "&nbsp;&nbsp;&nbsp;<span style=\"color:" + color + "\"> &#11044;&nbsp; &nbsp;" +
                "[" + event.getThreadName() + "]&nbsp;&nbsp;&nbsp;" + event.getMessage() + "</span></div>" + stacktrace;

        String htmlStore = sPrintableEvent;

        return htmlStore;
    }
}
