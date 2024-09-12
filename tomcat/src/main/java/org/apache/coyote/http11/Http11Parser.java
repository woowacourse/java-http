package org.apache.coyote.http11;

import java.util.Map.Entry;
import org.apache.coyote.http.response.HttpResponse;

public class Http11Parser {

    public static String writeHttpResponse(final HttpResponse response) {
        final StringBuilder serializedResponse = new StringBuilder();
        serializedResponse.append("HTTP/1.1 ").append(response.getStatusCode()).append(" \r\n");
        for (Entry<String, String> entry : response.getHeader().entrySet()) {
            serializedResponse.append("%s: %s \r\n".formatted(entry.getKey(), entry.getValue()));
        }
        if (response.getContentLength() != 0) {
            serializedResponse.append("\r\n");
            serializedResponse.append(response.getBody());
        }
        return serializedResponse.toString();
    }
}
