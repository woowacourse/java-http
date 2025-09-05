package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public record RequestStartLine(
        RequestMethod requestMethod,
        String requestUrl,
        String httpVersion
) {
    public Map<String, String> queryParameters() {
        int index = requestUrl.indexOf("?");
        String queryStrings = requestUrl.substring(index + 1);
        
        Map<String, String> queryParameters = new HashMap<>();
        for (String queryString : queryStrings.split("&")) {
            String[] strings = queryString.split("=");
            String key = strings[0];
            String value = strings[1];
            queryParameters.put(key, value);
        }

        return queryParameters;
    }
}
