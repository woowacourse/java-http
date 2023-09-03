package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String HEADER_DELIMITER = ": ";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_UTF_8 = "text/html;charset=utf-8";
    public static final String CONTENT_TYPE_CSS = "text/css";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String ACCEPT = "Accept";
    public static final String LOCATION = "Location";
    public static final String HTTP_LINE_SUFFIX = "\r\n";
    private static final String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders RestHeaders() {
        return new HttpHeaders(new HashMap<>(Map.of(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)));
    }

    public static HttpHeaders from(List<String> request) {
        Map<String, String> headers = request.stream()
                                             .skip(1)
                                             .takeWhile(line -> !line.isEmpty())
                                             .map(line -> line.split(HEADER_DELIMITER))
                                             .collect(Collectors.toMap(line -> line[0].trim(), line -> line[1].trim()));
        return new HttpHeaders(headers);
    }

    public boolean isAcceptCss() {
        return headers.get(ACCEPT).contains(CONTENT_TYPE_CSS);
    }

    public boolean isContentTypeUrlEncoded() {
        return headers.get(CONTENT_TYPE).contains(CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                      .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue())
                      .collect(Collectors.joining(HTTP_LINE_SUFFIX, "", HTTP_LINE_SUFFIX));
    }
}
