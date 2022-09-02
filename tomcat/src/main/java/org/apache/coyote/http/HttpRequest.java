package org.apache.coyote.http;

import static nextstep.jwp.views.RequestLoginUserOutput.printRequestLoginUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;

    private final Map<String, String> query;


    public HttpRequest(String request) {
        final String[] parseRequest = request.split(" ");
        this.httpMethod = HttpMethod.from(parseRequest[0]);
        int index = parseRequest[1].indexOf("?");
        this.url = parseUrl(parseRequest[1], index);
        this.query = parseQuery(parseRequest[1], index);
    }

    public void printUserLog(){
        if(this.url.equals("/login")){
            printRequestLoginUser(this);
        }
    }

    private String parseUrl(String uri, int index){
        if(hasQuery(index)){
            return uri.substring(0,index);
        }
        return uri;
    }

    private Map<String, String> parseQuery(String uri, int index) {
        if(hasQuery(index)){
            String queryString = uri.substring(index + 1);
            final String[] s = queryString.split("&");
            return Arrays.stream(s)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(key -> key[0], value -> value[1]));
        }
        return new HashMap<>();
    }

    private boolean hasQuery(int index) {
        return index >= 0;
    }

    public String getQueryByValue(String key){
        return query.get(key);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQuery() {
        return query;
    }
}
