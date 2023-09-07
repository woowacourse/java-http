package org.apache.coyote.http11.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {

    private static final String SUFFIX = " " + System.lineSeparator();
    private static final String DELIMITER = ": ";
    private final Map<HttpHeader, String> headersMap;

    public ResponseHeader(Map<HttpHeader, String> headersMap) {
        this.headersMap = headersMap;
    }

    public void put(HttpHeader field, String value) {
        headersMap.put(field, value);
    }

    public List<String> headerMessages() {
        return headersMap.keySet().stream()
                .map(httpHeader -> String.join(DELIMITER, httpHeader.headerName(), headersMap.get(httpHeader)))
                .collect(Collectors.toList());
    }

    public Map<HttpHeader, String> headersMap() {
        return headersMap;
    }
}
