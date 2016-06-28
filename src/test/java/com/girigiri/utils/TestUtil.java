package com.girigiri.utils;

import com.google.gson.Gson;
import org.springframework.http.MediaType;

/**
 * Created by JianGuo on 6/25/16.
 * Utils for Unit test
 */
public class TestUtil {
    private final static String SUBTYPE = "hal+json";

    public static MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            SUBTYPE);


    public static String objToJson(Object object) {
        return new Gson().toJson(object);
    }

    public static long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
