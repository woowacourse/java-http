package org.apache.catalina.servlet.util;

import java.util.HashMap;
import java.util.Map;

public class RequestParamUtil {

    public static Map<String, String> parse(String requestParams) {
        Map<String, String> result = new HashMap<>();
        String[] strings = requestParams.split("&");
        for (String string : strings) {
            String[] nameAndValue = string.split("=");
            result.put(nameAndValue[0], nameAndValue[1]);
        }
        return result;
    }
}
