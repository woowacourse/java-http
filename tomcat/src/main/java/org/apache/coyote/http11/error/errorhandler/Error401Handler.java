package org.apache.coyote.http11.error.errorhandler;

import java.util.HashMap;
import java.util.Map;

public class Error401Handler implements ErrorHandler {
    @Override
    public Map<String, String> handleError() {
        return resolveResponse(200, "/401.html");
    }

    private Map<String, String> resolveResponse(int statusCode, String viewUrl){
        Map<String, String> map = new HashMap<String, String>();
        map.put("statusCode", String.valueOf(statusCode));
        map.put("url", viewUrl);
        return map;
    }
}
