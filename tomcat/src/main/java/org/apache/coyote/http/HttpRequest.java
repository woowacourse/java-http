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

    private static final String QUERY_DELIMETER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String LOGIN_URL = "/login";
    private static final String INDEX_DELIMITER = "?";
    private final HttpMethod httpMethod;
    private final String url;

    private final Map<String, String> query;


    public HttpRequest(String request) {
        final String[] parseRequest = request.split(" ");
        this.httpMethod = HttpMethod.from(parseRequest[0]);
        Integer index = parseRequest[1].indexOf(INDEX_DELIMITER);
        this.url = parseUrl(parseRequest[1], index);
        this.query = parseQuery(parseRequest[1], index);
    }

    public void printUserLog(){
        if(this.url.equals(LOGIN_URL)){
            printRequestLoginUser(this);
        }
    }

    private String parseUrl(final String uri, final Integer index){
        if(hasQuery(index)){
            return uri.substring(0,index);
        }
        return uri;
    }

    private Map<String, String> parseQuery(final String uri, final Integer index) {
        if(hasQuery(index)){
            String queryString = uri.substring(index + 1);
            final String[] s = queryString.split(QUERY_DELIMETER);
            return Arrays.stream(s)
                    .map(q -> q.split(KEY_VALUE_DELIMITER))
                    .collect(Collectors.toMap(key -> key[0], value -> value[1]));
        }
        return new HashMap<>();
    }

    private boolean hasQuery(final int index) {
        return index >= 0;
    }

    public String getQueryByValue(final String key){
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
