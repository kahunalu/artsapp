package com.uvic.arts;


import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestManager {

    public static final String ARTS_GET_URL = "http://artsserver.herokuapp.com/content/getContent/";

    OkHttpClient okHttpClient;

    public RequestManager() {
        okHttpClient = new OkHttpClient();
    }

    public String getContent(String contentId) throws IOException {
        Request request = new Request.Builder()
                .url(ARTS_GET_URL + contentId)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }
}
