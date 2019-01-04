package com.mylive.project.loggy.mailGenerator;

import com.mylive.project.loggy.client.Client;
import com.mylive.project.loggy.client.ClientsRepository;
import com.mylive.project.loggy.event.Event;
import com.mylive.project.loggy.util.LoggyWebUtil;

import java.util.List;

/**
 * Created by sanjayda on 2/13/18 at 12:17 PM
 */

public class MailHandler {
    public static void sendMail(Event event, ClientsRepository clientsRepo, String sID) {
        List<String> lstSubscriber;
        try {
            if (null != event) {
                if (event.getEvent().name().equalsIgnoreCase("error") && (event.getStackTrace().size()>0 ) ) {
                    Client client = clientsRepo.findById(sID);
                    lstSubscriber = client.getLstErrorSubscriber();
                    if(lstSubscriber.size()!=0) {
                        LoggyReportGenerator.generateMailReport(LoggyWebUtil.appendToHtml(event), lstSubscriber, sID.replace("_"," "));
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Excetion ");
        }
    }
}
