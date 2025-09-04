package com.http.application;

import com.http.domain.HttpRequest;
import com.http.domain.HttpResponse;
import java.util.Map;

public final class HeaderResolver {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private HeaderResolver() {
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
            httpResponse.headers().put(CONTENT_TYPE, "text/html;charset=utf-8");
            return;
        }

        final String accept = headers.get("Accept").split(",")[0];
        httpResponse.headers().put(CONTENT_TYPE, accept);
    }

    private static void processContentLength(HttpResponse httpResponse) {
        if (httpResponse.body() == null) {
            return;
        }

        httpResponse.headers().put(CONTENT_LENGTH, String.valueOf(httpResponse.body().length));
    }
}
