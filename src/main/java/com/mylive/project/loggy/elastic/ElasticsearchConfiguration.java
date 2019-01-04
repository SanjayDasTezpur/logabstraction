
package com.mylive.project.loggy.elastic;

import com.mylive.project.loggy.socket.LoggyDBLogger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;



/**
 * Created by sanjayda on 2/28/18 at 6:30 PM
 */


@Configuration
@EnableElasticsearchRepositories(basePackages = "com.intel.swce.loggy.elastic")
public class ElasticsearchConfiguration
{
    private static final Logger logger = LoggerFactory.getLogger(LoggyDBLogger.class);

    @Value("${elasticsearch.host}")
    private String ESHost;

    @Value("${elasticsearch.port}")
    private int ESPort;

    @Value("${elasticsearch.clustername}")
    private String ESClusterName;


    @Bean
    public Client client(){

        try{
            Settings esSetting = Settings.builder()
                    .put("cluster.name",ESClusterName)
                    .put("client.transport.sniff", false)
                    .build();
            //TransportClient.builder().settings(esSetting).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ESHost), ESPort));
            TransportClient transportClient = new PreBuiltTransportClient(esSetting);
            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ESHost), ESPort));
            logger.info("Transport client" + transportClient.nodeName() + " for elastic is created and ready to sent datas ");
            return transportClient;

        } catch(Exception e){
            logger.error("Client not connected  "+e.getMessage());
            return null;
        }
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        ElasticsearchTemplate esTemp = new ElasticsearchTemplate(client());
        return esTemp;
    }



}