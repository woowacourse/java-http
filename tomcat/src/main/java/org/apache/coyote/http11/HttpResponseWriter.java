package org.apache.coyote.http11;

import java.util.Map.Entry;

public class HttpResponseWriter {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private HttpResponseWriter() {
    }

    public static String write(HttpResponse response) {
        StatusCode statusCode = response.getStatusCode();
        HttpHeader header = response.getHeader();
        String body = response.getBody();

        StringBuilder builder = new StringBuilder();
        builder.append("%s %s %s".formatted(HTTP_VERSION, statusCode.getCode(), statusCode.getMessage()));
        builder.append(CRLF);
        for (Entry<String, Object> entry : header.getFields().entrySet()) {
            builder.append("%s: %s".formatted(entry.getKey(), entry.getValue()));
            builder.append(CRLF);
        }
        builder.append("\r\n");
        builder.append(body);
        return builder.toString();
    }
}
