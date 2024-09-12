package org.apache.tomcat.util.http.header;

import static org.apache.catalina.connector.HttpResponse.NEW_LINE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class HttpHeaders {
    private static final String DELIMITER = ": ";
    private final Map<HttpHeaderType, String> headers;

    public HttpHeaders() {
        headers = new HashMap<>();
    }

    public HttpHeaders(Map<HttpHeaderType, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public boolean contains(HttpHeaderType header) {
        return headers.containsKey(header);
    }

    public String get(HttpHeaderType header) {
        return headers.get(header);
    }

    public void put(HttpHeaderType header, String value) {
        headers.put(header, value);
    }

    public String buildResponse() {
        StringBuilder response = new StringBuilder();
        for (Entry<HttpHeaderType, String> entry : headers.entrySet()) {
            response.append(entry.getKey().header()).append(DELIMITER).append(entry.getValue()).append(NEW_LINE);
        }
        return response.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HttpHeaders that = (HttpHeaders) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(headers);
    }
}
