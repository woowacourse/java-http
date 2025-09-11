package org.apache.coyote.util;

import java.util.Map;
import org.apache.coyote.dto.HttpResponse;

public class HttpResponseBuilder {

    public static void staticResponse(
            String version,
            int statusCode,
            String contentType,
            String content,
            HttpResponse response
    ) {
        response.setVersion(version);
        response.setStatusCode(statusCode);
        response.setHeaders(null);
        response.setContentType(contentType);
        response.setBody(content);
    }

    public static void redirectResponse(
            String version,
            int statusCode,
            Map<String, String> headers,
            HttpResponse response
    ) {
        response.setVersion(version);
        response.setStatusCode(statusCode);
        response.setHeaders(headers);
        response.setContentType(null);
        response.setBody("");
    }

}
