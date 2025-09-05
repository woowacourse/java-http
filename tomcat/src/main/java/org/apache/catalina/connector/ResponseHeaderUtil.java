package org.apache.catalina.connector;

import java.util.Map;
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
        if (httpRequest.headers() == null) {
            return;
        }

        final Map<String, String> headers = httpRequest.headers();
        if (!containNormalizeHeaderKey(headers, ACCEPT)) {
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            return;
        }

        final String accept = headers.get(ACCEPT).split(",")[0];
        httpResponse.addHeader(CONTENT_TYPE, accept);
    }

    private static void processContentLength(HttpResponse httpResponse) {
        if (httpResponse.getBody() == null) {
            return;
        }

        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(httpResponse.getBody().length));
    }

    // TODO Header 객체로 만들어서 처리하는게 좋을 거 같음
    private static boolean containNormalizeHeaderKey(Map<String, String> headers, String key) {
        return headers.keySet().stream()
                .anyMatch(k -> normalizeHeaderKey(k).equals(normalizeHeaderKey(key)));
    }

    private static String normalizeHeaderKey(String key) {
        String[] tokens = key.split("-");
        StringBuilder builder = new StringBuilder();
        for (String token : tokens) {
            builder.append(Character.toUpperCase(token.charAt(0)))
                    .append(token.substring(1).toLowerCase())
                    .append("-");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
