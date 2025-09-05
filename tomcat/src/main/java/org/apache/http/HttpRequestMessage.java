package org.apache.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.exception.InvalidRequestException;

public class HttpRequestMessage {

    private final HttpMethod method;
    private final String uri;
    private final Map<String, String> queryString;
    private final HttpVersion version;

    public HttpRequestMessage(List<String> message) {
        List<String> startLine = List.of(message.getFirst().split(" "));
        if (startLine.size() < 3) {
            throw new InvalidRequestException("요청 메세지의 시작라인 형식이 올바르지 않습니다.");
        }
        method = HttpMethod.valueOf(startLine.get(0));
        uri = parseUri(startLine.get(1));
        queryString = parseQueryParam(startLine.get(1));
        version = HttpVersion.parse(startLine.get(2));
    }

    public boolean checkQueryStringExistence(String key) {
        return queryString.containsKey(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String getQueryString(String key) {
        return queryString.get(key);
    }

    private String parseUri(String uriLine) {
        List<String> startLinePart = List.of(uriLine.split("\\?"));
        return startLinePart.getFirst();
    }

    private Map<String, String> parseQueryParam(String uriLine) {
        HashMap<String, String> queryStrings = new HashMap<>();

        List<String> startLinePart = List.of(uriLine.split("\\?"));
        if (startLinePart.size() < 2) {
            return queryStrings;
        }

        String queryStringLine = startLinePart.getLast();
        List<String> queryStringParts = List.of(queryStringLine.split("&"));
        for (String queryStringPart : queryStringParts) {
            List<String> keyValue = List.of(queryStringPart.split("="));
            queryStrings.put(keyValue.getFirst(), keyValue.getLast());
        }

        return queryStrings;
    }
}
