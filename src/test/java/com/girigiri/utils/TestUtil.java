package com.girigiri.utils;

import com.google.gson.Gson;

/**
 * Created by JianGuo on 6/25/16.
 */
public class TestUtil {
    public static String objToJson(Object object) {
        return new Gson().toJson(object);
    }
}
