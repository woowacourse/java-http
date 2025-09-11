package org.apache.coyote.util;

import java.util.Map;
import org.apache.coyote.dto.HttpResponse;

public class HttpResponseBuilder {

    public static HttpResponse staticResponse(String version, int statusCode, String contentType, String content) {
        return new HttpResponse(
                version,
                statusCode,
                null,
                contentType,
                content
        );
    }

    public static HttpResponse redirectResponse(String version ,int statusCode, Map<String,String> headers) {
        return new HttpResponse(
                version,
                statusCode,
                headers,
                null,
                null
        );
    }
}
