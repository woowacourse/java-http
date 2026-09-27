package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

public record HttpRequest(RequestLine requestLine, HttpHeaders headers, byte[] body, Session session) {

    public Map<String, String> getParseBodyQuery() {
        final Map<String, String> queryPairs = new HashMap<>();
        for (String queryPair : new String(body).split("&")) {
            final int splitIndex = queryPair.indexOf("=");
            final String key = queryPair.substring(0, splitIndex).trim();
            final String value = queryPair.substring(splitIndex + 1).trim();
            queryPairs.put(key, value);
        }
        return queryPairs;
    }
}
