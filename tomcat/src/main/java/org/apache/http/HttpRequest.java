package org.apache.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.exception.InvalidRequestException;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final Map<String, String> queryString;
    private final HttpVersion version;

    public HttpRequest(List<String> message) {
        validateEmptyMessage(message);
        validateStartLineFormat(message.getFirst());

        List<String> startLine = List.of(message.getFirst().split(" "));
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

        if (!hasQueryParam(uriLine)) {
            return queryStrings;
        }

        List<String> startLinePart = List.of(uriLine.split("\\?"));
        String queryStringLine = startLinePart.getLast();
        List<String> queryStringParts = List.of(queryStringLine.split("&"));
        for (String queryStringPart : queryStringParts) {
            List<String> keyValue = List.of(queryStringPart.split("="));
            queryStrings.put(keyValue.getFirst(), keyValue.getLast());
        }
        return queryStrings;
    }

    private void validateEmptyMessage(List<String> message) {
        if (message.isEmpty()) {
            throw new InvalidRequestException("요청 메세지가 비어있습니다.");
        }
    }

    private void validateStartLineFormat(String startLine) {
        List<String> startLinePart = List.of(startLine.split(" "));
        if (startLinePart.size() < 3) {
            throw new InvalidRequestException("요청 메세지의 시작라인 형식이 올바르지 않습니다.");
        }
    }

    private boolean hasQueryParam(String uriLine) {
        return uriLine.contains("?")
                && uriLine.indexOf("?") != uriLine.length() - 1;
    }
}
