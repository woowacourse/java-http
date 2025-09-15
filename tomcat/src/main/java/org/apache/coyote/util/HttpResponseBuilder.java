package org.apache.coyote.util;

import org.apache.coyote.dto.HttpHeader;
import org.apache.coyote.dto.HttpResponse;

public class HttpResponseBuilder {

    public static void initResponse(
            String version,
            int statusCode,
            HttpHeader httpHeader,
            String content,
            HttpResponse response
    ) {
        response.setVersion(version);
        response.setStatusCode(statusCode);
        response.setHeaders(httpHeader);
        response.setBody(content);
    }
}
