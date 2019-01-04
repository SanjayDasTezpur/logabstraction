package com.mylive.project.loggy.socket;

import com.mylive.project.loggy.client.ClientsRepository;
import com.mylive.project.loggy.elastic.ElasticEventOperations;
import com.mylive.project.loggy.youtrack.YouTrackFileBug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

 /*
 * Created by sanjayda on 2/6/18 at 1:49 PM

*/
@Component
public class LoggySocketServerRunner implements ApplicationRunner {

     private static final Logger logger = LoggerFactory.getLogger(LoggySocketServerRunner.class);

     ServerSocket serverSocket;
     private int PORT = 3389;

     @Autowired
     JdbcTemplate jt;

     @Autowired
     ElasticEventOperations eventOperations;

     @Autowired
     ClientsRepository clientsRepository;

     @Autowired
     YouTrackFileBug youTrackFileBug;

     @Autowired
     public LoggySocketServerRunner() {
     }

     @Override
     public void run(ApplicationArguments args) throws Exception {
         try {
             serverSocket = new ServerSocket(PORT);
             logger.info("Agent Server started on PORT :: "+PORT);
         } catch (IOException e) {
             logger.info("Agent Server not started");
         }
         try {
             while (true) {
                 Socket socket = serverSocket.accept();
                 logger.info(socket.getInetAddress().getHostAddress()+"["+ socket.getInetAddress().getHostName() + "]"+ " client connected");
                 LoggyDBLogger target = new LoggyDBLogger(socket, jt, clientsRepository,eventOperations,youTrackFileBug);
                 new Thread(target).start();
             }
         } catch (IOException e) {
             logger.info("Client Could not connect");
         }

     }
 }
