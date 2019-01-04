package com.mylive.project.loggy.parsing;

import com.mylive.project.loggy.event.EEvent;
import com.mylive.project.loggy.event.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pjpandey on 11/14/17.
 */
public class LoggyParser {


    static String sRegex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})(,\\d{3}) \\[(.*)\\] ([^ ]*) +([^ ]*) - (.*)$";

    public static Event parse(String sLine) {
        Pattern pattern = Pattern.compile(sRegex);
        Matcher matcher = pattern.matcher(sLine.trim());
        Event event = new Event();
        if(matcher.matches()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(matcher.group(1));
                event.setTimeStamp(date);
                event.setThreadName(matcher.group(3));
                event.setEvent(EEvent.valueOf(matcher.group(4)));
                event.setPackageName(matcher.group(5));
                event.setMessage(matcher.group(6).replace("'", "\\'"));
            } catch (Exception e) {
               // logger.error("got Exception while parsing line : " + sLine + "\n" +e.getMessage());
            }
        }
        return event;
    }

    public static boolean isEvent(String sLine) {
        Pattern pattern = Pattern.compile(sRegex);
        Matcher matcher = pattern.matcher(sLine.trim());
        return matcher.matches();
    }
}
