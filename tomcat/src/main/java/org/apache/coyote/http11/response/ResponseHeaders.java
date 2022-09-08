package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private static final String LOCATION_HEADER = "Location";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String HOST = "http://localhost:8080/";

    private final Map<String, String> values = new LinkedHashMap<>();

    public void setHeaders(ResponseEntity responseEntity) {
        if (responseEntity.getHttpStatus().isRedirect()) {
            String responseBody = responseEntity.getResponseBody();
            values.put(LOCATION_HEADER, HOST + responseBody.split(":")[1]);
            return;
        }
        ContentType contentType = ContentType.findContentType(responseEntity.getResponseBody());
        values.put(CONTENT_TYPE_HEADER, "text/" + contentType.name().toLowerCase() + ";charset=utf-8");
    }

    public void add(String key, String value) {
        values.put(key, value);
    }

    public String asString() {
        return this.values.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
