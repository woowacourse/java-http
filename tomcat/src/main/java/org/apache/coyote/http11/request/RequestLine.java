package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private String method;
    private String path;
    private Map<String, String> queryParameters = new HashMap<>();

    public RequestLine(String requestLine) {
        String inputQueryString = "";
        int queryStringIndex = requestLine.indexOf("?");
        if (queryStringIndex >= 0) {
            path = requestLine.substring(0, queryStringIndex);
            inputQueryString = requestLine.substring(queryStringIndex)
                    .replace("?", "");
        }

        Arrays.stream(inputQueryString.split("&"))
                .forEach(queryParameterEntry -> {
                    String[] split = queryParameterEntry.split("=");
                    if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                        queryParameters.put(split[0], split[1]);
                    }
                });
    }

    public boolean isGetRequest() {
        return "GET".equals(method);
    }

    public boolean isPostRequest() {
        return "POST".equals(method);
    }
}
