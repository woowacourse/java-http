package org.apache.tomcat.util.http.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.core.exception.InvalidRequestException;

public class RequestQueryStrings {

    private final Map<String, String> queryStrings;

    public RequestQueryStrings(String startLine) {
        this.queryStrings = parseQueryStrings(startLine);
    }

    public boolean containKey(String key) {
        return queryStrings.containsKey(key);
    }

    public String getValue(String key) {
        return queryStrings.get(key);
    }

    private Map<String, String> parseQueryStrings(String startLine) {
        Map<String, String> queryStringRead = new HashMap<>();

        String uriLine = parseUriLine(startLine);
        if (!hasQueryParam(uriLine)) {
            return queryStringRead;
        }

        int separatorIndex = uriLine.indexOf("?");
        String queryStringLine = uriLine.substring(separatorIndex + 1);
        List<String> queryStringParts = List.of(queryStringLine.split("&"));
        for (String queryStringPart : queryStringParts) {
            List<String> keyValue = List.of(queryStringPart.split("="));
            queryStringRead.put(keyValue.getFirst(), keyValue.getLast());
        }
        return queryStringRead;
    }

    private String parseUriLine(String startLine) {
        List<String> startLinePart = List.of(startLine.split("\\s+"));
        if (startLinePart.size() < 3) {
            throw new InvalidRequestException("유효하지 않은 요청의 StartLine입니다.");
        }
        return URLDecoder.decode(startLinePart.get(1), StandardCharsets.UTF_8);
    }

    private boolean hasQueryParam(String uriLine) {
        return uriLine.contains("?")
                && uriLine.indexOf("?") != uriLine.length() - 1;
    }
}
