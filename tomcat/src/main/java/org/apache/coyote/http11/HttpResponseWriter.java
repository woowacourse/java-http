package org.apache.coyote.http11;

import java.util.Map.Entry;

public class HttpResponseWriter {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private HttpResponseWriter() {
    }

    public static String write(HttpResponse response) {
        StatusCode statusCode = response.getStatusCode();
        HttpHeaders headers = response.getHeaders();
        String content = response.getContent();

        StringBuilder builder = new StringBuilder();
        builder.append("%s %s %s".formatted(HTTP_VERSION, statusCode.getCode(), statusCode.getMessage()));
        builder.append(CRLF);
        for (Entry<String, Object> entry : headers.getFields().entrySet()) {
            builder.append("%s: %s".formatted(entry.getKey(), entry.getValue()));
            builder.append(CRLF);
        }
        builder.append("\r\n");
        builder.append(content);
        return builder.toString();
    }
}
