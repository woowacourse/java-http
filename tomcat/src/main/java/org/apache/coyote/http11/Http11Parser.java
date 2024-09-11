package org.apache.coyote.http11;

import java.util.Map.Entry;
import org.apache.coyote.HttpResponse;

public class Http11Parser {

    public static String writeHttpResponse(final HttpResponse response) {
        final StringBuilder serializedResponse = new StringBuilder();
        serializedResponse.append("HTTP/1.1 ").append(response.getStatusCode()).append(" \r\n");
        for (Entry<String, String> entry : response.getHeader().entrySet()) {
            serializedResponse.append("%s: %s \n".formatted(entry.getKey(), entry.getValue()));
        }
        if (response.getLocation() != null) {
            serializedResponse.append("Location: ").append(response.getLocation()).append(" \r\n");
            serializedResponse.append("Content-Length: 0 \r\n");
            serializedResponse.append("\r\n");
        }
        if (response.getContent() != null) {
            serializedResponse.append("Content-Type: ").append(response.getContentType()).append(";charset=utf-8 \r\n");
            serializedResponse.append("Content-Length: ").append(response.getContentLength()).append(" \r\n");
            serializedResponse.append("\r\n");
            serializedResponse.append(response.getBody());
        }
        return serializedResponse.toString();
    }
}
