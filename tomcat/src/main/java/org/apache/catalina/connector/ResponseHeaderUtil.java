package org.apache.catalina.connector;

import org.apache.catalina.domain.HttpHeader;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;

public final class ResponseHeaderUtil {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String ACCEPT = "Accept";

    private ResponseHeaderUtil() {
    }

    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        processContentType(httpRequest, httpResponse);
        processContentLength(httpResponse);
    }

    private static void processContentType(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (isStandardContentType(httpRequest, httpResponse)) {
            return;
        }

        applyContentTypeFromAccept(httpRequest, httpResponse);
    }

    private static boolean isStandardContentType(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.requestStartLine().path().endsWith(".html")) {
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            return true;
        }

        if (httpRequest.requestStartLine().path().endsWith(".css")) {
            httpResponse.addHeader(CONTENT_TYPE, "text/css");
            return true;
        }

        if (httpRequest.requestStartLine().path().endsWith(".js")) {
            httpResponse.addHeader(CONTENT_TYPE, "application/javascript");
            return true;
        }

        return false;
    }

    private static void applyContentTypeFromAccept(HttpRequest httpRequest, HttpResponse httpResponse) {
        final HttpHeader header = httpRequest.header();
        if (header == null) {
            return;
        }

        if (!header.containKey(CONTENT_TYPE)) {
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            return;
        }

        final String accept = header.get(ACCEPT).split(",")[0];
        httpResponse.addHeader(CONTENT_TYPE, accept);
    }

    private static void processContentLength(HttpResponse httpResponse) {
        if (httpResponse.getBody() == null) {
            return;
        }

        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(httpResponse.getBody().length));
    }
}
