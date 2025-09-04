package org.apache.catalina.connector;

import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import java.util.Map;

public final class ResponseHeaderUtil {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private ResponseHeaderUtil() {
    }

    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.headers() == null) {
            return;
        }

        processContentType(httpRequest, httpResponse);
        processContentLength(httpResponse);
    }

    private static void processContentType(HttpRequest httpRequest, HttpResponse httpResponse) {
        final Map<String, String> headers = httpRequest.headers();
        if (!headers.containsKey("Accept")) {
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            return;
        }

        final String accept = headers.get("Accept").split(",")[0];
        httpResponse.addHeader(CONTENT_TYPE, accept);
    }

    private static void processContentLength(HttpResponse httpResponse) {
        if (httpResponse.getBody() == null) {
            return;
        }

        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(httpResponse.getBody().length));
    }
}
