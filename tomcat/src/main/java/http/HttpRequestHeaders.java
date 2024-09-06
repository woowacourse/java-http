package http;

import http.requestheader.Accept;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeaders {

    private static final String HEADER_FIELD_DELIMITER = ":[ |\t]";

    private final Map<String, String> headers;

    public HttpRequestHeaders(List<String> headerValues) {
        headers = new HashMap<>();
        for (String headerValue : headerValues) {
            String[] headerParts = headerValue.split(HEADER_FIELD_DELIMITER, -1);
            String fieldName = headerParts[0];
            String fieldValue = headerParts[1];
            headers.put(fieldName, fieldValue);
        }
    }

    public Accept getAcceptValue() {
        if (headers.containsKey("Accept")) {
            return new Accept(headers.get("Accept"));
        }
        return new Accept();
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }
}
