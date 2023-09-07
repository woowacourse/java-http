package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.MimeType;

public class ResponseHeaders {

    private final Map<String, String> headers;
    private final HttpCookie httpCookie;

    public ResponseHeaders(Map<String, String> headers, HttpCookie httpCookie) {
        this.headers = headers;
        this.httpCookie = httpCookie;
    }

    public static ResponseHeaders from(ResponseEntity responseEntity, MimeType mimeType) {
        String path = responseEntity.getPath();
        Map<String, String> headers = makeHeaders(responseEntity.getHttpStatus(), mimeType, path);
        return new ResponseHeaders(headers, responseEntity.getHttpCookie());
    }

    private static Map<String, String> makeHeaders(HttpStatus httpStatus, MimeType mimeType, String path) {
        Map<String, String> headers = new HashMap<>();

        if (httpStatus.equals(HttpStatus.FOUND)) {
            headers.put("Content-Type", convertContentType(MimeType.HTML));
            headers.put("Location", path);
        }

        headers.put("Content-Type", convertContentType(mimeType));
        return headers;
    }

    private static String convertContentType(MimeType mimeType) {
        return String.format("%s;charset=utf-8 ", mimeType.getContentType());
    }

    public StringBuilder convertResponseHeaders() {
        return new StringBuilder()
                .append(httpCookie.getCookies())
                .append(getHeaders());
    }

    private String getHeaders() {
        return headers.entrySet().stream()
                .map(this::formatHeaders)
                .collect(Collectors.joining("\r\n"));
    }

    private String formatHeaders(Map.Entry<String, String> entry) {
        return String.join(": ", entry.getKey(), entry.getValue());
    }

}
