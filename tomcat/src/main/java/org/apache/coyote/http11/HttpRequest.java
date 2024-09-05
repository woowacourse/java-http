package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

class HttpRequest {

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryString;
    private final String body;

    public HttpRequest(String request) {
        String[] firstLineArgs = request.split(System.lineSeparator())[0].split(" ");
        this.httpMethod = firstLineArgs[0];
        this.path = initializePath(firstLineArgs[1]);
        this.queryString = initializeQueryString(firstLineArgs[1]);
        this.body = initializeBody(request);
    }

    private String initializePath(String url) {
        if (url.contains("?")) {
            return url.split("[?]")[0];
        }
        return url;
    }

    private Map<String, String> initializeQueryString(String url) {
        Map<String, String> map = new HashMap<>();
        if (url.contains("?")) {
            String[] queryStringArgs = url.split("[?]")[1].split("[&=]");
            for (int i = 0; i < queryStringArgs.length; i++) {
                map.put(queryStringArgs[i * 2], queryStringArgs[i * 2 + 1]);
            }
        }

        return map;
    }

    private String initializeBody(String request) {
        String[] requests = request.split(System.lineSeparator() + System.lineSeparator());
        if (requests.length == 2) {
            return requests[1];
        }

        return null;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public String getPath() {
        return this.path;
    }

    public String getQueryStringValue(String key) {
        return this.queryString.get(key);
    }

    public String getBody() {
        return this.body;
    }
}
