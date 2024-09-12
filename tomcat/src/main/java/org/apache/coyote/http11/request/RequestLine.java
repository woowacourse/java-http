package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private String method;
    private String path;
    private Map<String, String> queryParameters;

    public static RequestLine create(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");
        String method = splitRequestLine[0];
        String path = splitRequestLine[1];

        String inputQueryString = "";
        int queryStringIndex = requestLine.indexOf("?");
        if (queryStringIndex >= 0) {
            path = requestLine.substring(0, queryStringIndex);
            inputQueryString = requestLine.substring(queryStringIndex)
                    .replace("?", "");
        }

        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(inputQueryString.split("&"))
                .forEach(queryParameterEntry -> {
                    String[] split = queryParameterEntry.split("=");
                    if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                        queryParameters.put(split[0], split[1]);
                    }
                });

        return new RequestLine(method, path, queryParameters);
    }

    private RequestLine(String method, String path, Map<String, String> queryParameters) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public boolean isGetRequest() {
        return "GET".equals(method);
    }

    public boolean isPostRequest() {
        return "POST".equals(method);
    }

    public String getPath() {
        return this.path;
    }
}
