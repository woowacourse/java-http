package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;

public class QueryParamBinderUtil {

    public static Map<String, String> bind(String queryParam) {
        Map<String, String> result = new HashMap<>();
        String[] params = queryParam.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }
}
