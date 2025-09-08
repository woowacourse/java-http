package org.apache.catalina.controller.util;

import java.util.HashMap;
import java.util.Map;

public class QueryParam {

    public static Map<String, String> getQueryParams(String requestBody){
        Map<String, String> bodyValues = new HashMap<>();
        String[] values = requestBody.split("&");
        for (String value : values) {
            final String[] split = value.split("=");

            bodyValues.put(split[0], split[1]);
        }

        return bodyValues;
    }

}
