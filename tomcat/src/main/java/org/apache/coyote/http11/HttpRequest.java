package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.constant.RequestLine;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final String body;

    public HttpRequest(RequestLine requestLine, Map<String, String> header, String body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> parseBody() {
        return parseQueryString(body);
    }

    private static Map<String, String> parseQueryString(String queryString) {
        final Map<String, String> parsed = new HashMap<>();
        final String[] fields = queryString.split("&");
        for (String field : fields) {
            final int delimiterIndex = field.indexOf("=");
            final String fieldName = field.substring(0, delimiterIndex);
            final String fieldValue = field.substring(delimiterIndex + 1);
            parsed.put(fieldName, fieldValue);
        }
        return parsed;
    }
}
