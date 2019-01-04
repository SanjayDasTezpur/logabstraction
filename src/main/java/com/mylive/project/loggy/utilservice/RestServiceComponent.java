package com.mylive.project.loggy.utilservice;

import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RestServiceComponent {
    private static final Logger logger = LoggerFactory.getLogger(RestServiceComponent.class);
    private static final String TOKEN = "ENTER YOUR TOKEN HERE";
    OkHttpClient client;

    public RestServiceComponent() {
        client = new OkHttpClient();
        logger.info("RestServiceComponent is up now");
    }

    public Request.Builder buildRequest(String url) {
        RequestBody body = RequestBody.create(null, new byte[0]);
        Request.Builder request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + TOKEN)
                .url(url); // "http://localhost:8081/clients"
        return request;
    }
    public Response runPutWithEmptyBOdy(String url)
    {
        Request.Builder builder = buildRequest(url);
        builder.put(RequestBody.create(null, new byte[0]));
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            logger.error("IOException "+e.getMessage());
            return null;
        }
    }
    public Response runPost(String url, String body)
    {
        MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        Request.Builder builder = buildRequest(url);
        builder.post(RequestBody.create(FORM, body));
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            logger.error("IOException "+e.getMessage());
            return null;
        }
    }
    public Response runGet(String url)
    {
        Request.Builder builder = buildRequest(url);
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            logger.error("IOException "+e.getMessage());
            return null;
        }
    }
}
