package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final StartLine startline;
    private final Map<String, String> headers;
    private final QueryParams queryParams;

    public HttpRequest(StartLine startline, List<String> headerLines, QueryParams queryParams) {
        this.startline = startline;
        this.headers = parsingHeader(headerLines);
        this.queryParams = queryParams;
    }

    public Map<String, String> parsingHeader(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();
        for (String headerLine : headerLines) {
            String[] item = headerLine.split(": ");
            String key = item[0];
            String value = item[1];
            headers.put(key, value);
        }
        return headers;
    }

    public boolean isMainRequest() {
        return startline.isMainRequest();
    }

    public boolean hasParams() {
        return !queryParams.isEmpty();
    }

    public String getRequestURL() {
        return startline.getRequestURL();
    }
}
