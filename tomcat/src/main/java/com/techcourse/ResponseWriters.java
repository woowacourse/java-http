package com.techcourse;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import org.apache.coyote.http11.resource.ResourceUtil;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class ResponseWriters {

    public static void ok(HttpResponse httpResponse, String resourcePath) throws IOException {
        URL url = ResourceUtil.resolve(resourcePath);
        if (url == null) {
            notFound(httpResponse);
        }
        byte[] bytes = ResourceUtil.readAll(url);
        String contentType = URLConnection.guessContentTypeFromName(url.toString());
        ok(httpResponse, bytes, contentType);
    }

    public static void ok(HttpResponse httpResponse, byte[] bytes, String contentType) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setBody(bytes);
        httpResponse.addHeader(contentType, bytes);
    }

    public static void ok(HttpResponse httpResponse, byte[] bytes) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setBody(bytes);
        httpResponse.addHeader(bytes);
    }

    public static void ok(HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.addHeader(); // TODO 2025. 9. 12. 04:21: defaultheader의 책임 분리 : ResponseWriters vs HttpResponse
    }

    public static void found(HttpResponse httpResponse, String redirectUrl, String sessionId) {
        httpResponse.setStatus(HttpStatus.FOUND);
        Map<String, String> headers = Map.of(
                "Location", redirectUrl,
                "Set-Cookie", "JSESSIONID=" + sessionId
        );

        httpResponse.addHeader(headers);
    }

    public static void found(HttpResponse httpResponse, String redirectUrl) {
        httpResponse.setStatus(HttpStatus.FOUND);
        Map<String, String> headers = Map.of(
                "Location", redirectUrl
        );

        httpResponse.addHeader(headers);
    }

    public static void notFound(HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.NOT_FOUND);
    }
}
