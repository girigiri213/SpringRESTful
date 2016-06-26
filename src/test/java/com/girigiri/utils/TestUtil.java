package com.girigiri.utils;

import com.google.gson.Gson;

/**
 * Created by JianGuo on 6/25/16.
 * Utils for Unit test
 */
public class TestUtil {
    public static String objToJson(Object object) {
        return new Gson().toJson(object);
    }

    public static long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
