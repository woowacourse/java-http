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
        this.path = firstLineArgs[1].split("[?]")[0];

        Map<String, String> map = new HashMap<>();
        String[] queryStringArgs = firstLineArgs[1].split("[?]")[1].split("[&=]");
        for (int i = 0; i < queryStringArgs.length; i++) {
            map.put(queryStringArgs[i * 2], queryStringArgs[i * 2 + 1]);
        }
        this.queryString = map;

        this.body = request.split(System.lineSeparator() + System.lineSeparator())[1];
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
