
package com.mylive.project.loggy.elastic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mylive.project.loggy.event.Event;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by sanjayda on 3/1/18 at 1:30 PM
 */
@Component
public class ElasticEventOperations {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static final Logger logger = LoggerFactory.getLogger(ElasticEventOperations.class);
    @Autowired
    private ElasticEventServiceImpl eservice;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    public ElasticEventOperations() {
        logger.info(getClass() + " Created and Elastic API is ready to sent data to elastic server");
    }


    public void before()
    {
        esTemplate.deleteIndex(ElasticEvent.class);
        esTemplate.createIndex(ElasticEvent.class);
        esTemplate.putMapping(ElasticEvent.class);
        esTemplate.refresh(ElasticEvent.class);
    }

    public ElasticEvent makeElasticEvent(Event event)
    {
        ElasticEvent eevent = new ElasticEvent();

        eevent.setId(event.getUniqueID());
        eevent.setEvent(event.getEvent());
        eevent.setMessage(event.getMessage());
        eevent.setPackageName(event.getPackageName());
        eevent.setThreadName(event.getThreadName());
        eevent.setStackTrace(event.getStackTrace());
        eevent.setTimeStamp(event.getTimeStamp());
        /*try {
            ElasticEvent retEvent = eservice.save(eevent);
        }catch (Exception e){
            logger.error("Exception found in Elastic saving operation \n"+e.getMessage());
        }*/
        return eevent;
    }

    public void saveBulkElasticEvent(String indexName, String type, Event event)
    {
        ElasticEvent eevent = makeElasticEvent(event);
        try {
            BulkRequestBuilder requestBuilder = esTemplate.getClient().prepareBulk();
            IndexRequestBuilder request = esTemplate.getClient().prepareIndex(indexName, event.getEvent().name());
            request.setIndex(indexName);
            request.setType(event.getEvent().name());
            //request.setId(event.getUniqueID());
            request.setSource(gson.toJson(eevent), XContentType.JSON);

            requestBuilder.add(request);

            BulkResponse bulkResponse = requestBuilder.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                logger.error("Failed to insert data in elastic \n" + bulkResponse.buildFailureMessage());
            }
        } catch (NoNodeAvailableException e ){
            logger.error("Failed to insert data in elastic NoNodeAvailableException Caught " + e.getMessage());
        }catch (Exception e ){
            logger.error("Failed to insert data in elastic Exception Caught " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean createElasticIndex(String indexName, String type)
    {
        String srcMapJson = getSourceMapping();
        /*String srcMapJson = "{\n" +
                "        \"properties\" : {\n" +
                "          \"stackTrace\" : {\n" +
                "            \"type\" : \"nested\"\n" +
                "          }\n" +
                "        }\n" +
                "      }";*/

        CreateIndexRequest indexRequest = null;
        try {
            indexRequest = new CreateIndexRequest(indexName);
            indexRequest.settings(Settings.EMPTY);

        }catch (IllegalArgumentException e){
            logger.error( indexName+" already exists \n"+e.getMessage());
        }
        catch (Exception e){
            logger.error("Caught exception : "+e.getMessage());
        }
        if(null == indexRequest){
            logger.error("" + indexRequest + "is Null");
            return false;
        }
        if( esTemplate.getClient().admin().indices().prepareExists(indexName).get().isExists() ){
            return true;
        }
        CreateIndexResponse response = esTemplate.getClient().admin().indices().create(indexRequest).actionGet();
        if (!response.isAcknowledged())
        {
            logger.error("Error creating index: " + indexName);
            return false;
        }
        PutMappingRequestBuilder putMappingBuilder = esTemplate.getClient().admin().indices().preparePutMapping(indexName);
        putMappingBuilder.setType("INFO");
        putMappingBuilder.setSource(srcMapJson, XContentType.JSON);
        PutMappingResponse putMappingResponse = putMappingBuilder.get();
        if (!putMappingResponse.isAcknowledged())
        {
            logger.error("Error creating index: " + indexName + " with type INFO");
            return false;
        }
        putMappingResponse = esTemplate.getClient().admin().indices().preparePutMapping(indexName)
                .setType("WARN")
                .setSource(srcMapJson, XContentType.JSON)
                .get();

        if (!putMappingResponse.isAcknowledged())
        {
            logger.error("Error creating index: " + indexName + " with type WARN");
            return false;
        }
        putMappingResponse = esTemplate.getClient().admin().indices().preparePutMapping(indexName)
                .setType("ERROR")
                .setSource(srcMapJson, XContentType.JSON)
                .get();

        if (!putMappingResponse.isAcknowledged())
        {
            logger.error("Error creating index: " + indexName + " with type ERROR");
            return false;
        }
        putMappingResponse = esTemplate.getClient().admin().indices().preparePutMapping(indexName)
                .setType("DEBUG")
                .setSource(srcMapJson, XContentType.JSON)
                .get();

        if (!putMappingResponse.isAcknowledged())
        {
            logger.error("Error creating index: " + indexName + " with type DEBUG");
            return false;
        }
        putMappingResponse = esTemplate.getClient().admin().indices().preparePutMapping(indexName)
                .setType("FATAL")
                .setSource(srcMapJson, XContentType.JSON)
                .get();

        if (!putMappingResponse.isAcknowledged())
        {
            logger.error("Error creating index: " + indexName + " with type FATAL");
            return false;
        }

        return true;

    }

    private String getSourceMapping(){
        return "{\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"string\",\"index\": \"not_analyzed\"\n" +
                "    },\n" +
                "    \"timeStamp\": {\n" +
                "      \"type\": \"string\",\"index\": \"not_analyzed\"\n" +
                "    },\n" +
                "    \"threadName\": {\n" +
                "      \"type\": \"string\",\"index\": \"not_analyzed\"\n" +
                "    },\n" +
                "    \"event\": {\n" +
                "      \"type\": \"string\",\"index\": \"not_analyzed\"\n" +
                "    },\n" +
                "    \"packageName\": {\n" +
                "      \"type\": \"string\",\"index\": \"not_analyzed\"\n" +
                "    },\n" +
                "    \"message\": {\n" +
                "      \"type\": \"string\",\"index\": \"not_analyzed\"\n" +
                "    },\n" +
                "    \"stackTrace\": {\n" +
                "      \"type\": \"string\",\"index\": \"not_analyzed\"\n" +
                "    }\n" +
                "    \n" +
                "  }\n" +
                "}";
    }
}
