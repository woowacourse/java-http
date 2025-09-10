package org.apache.coyote.util;

import com.spring.http.enums.HttpStatus;
import java.io.IOException;
import org.apache.catalina.connector.ResponseHeaderUtil;
import org.apache.catalina.domain.request.HttpRequest;
import org.apache.catalina.domain.response.HttpResponse;
import org.apache.catalina.util.FileParser;

public final class ResponseUtil {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private ResponseUtil() {
    }

    public static void handle(HttpRequest request, HttpResponse response) {
        response.setVersion(getVersion(request));

        ResponseHeaderUtil.handle(request, response);
    }

    public static void handleBadRequest(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST);
        handleErrorPage(request, response);
    }

    public static void handleErrorPage(HttpRequest request, HttpResponse response)
            throws IOException {
        response.setVersion(getVersion(request));
        response.addHeader("Connection", "close");

        final byte[] bytes = FileParser.loadErrorPage(response.getStatus());
        response.setBody(bytes);
    }

    private static String getVersion(HttpRequest request) {
        if (request == null || request.requestStartLine() == null) {
            return DEFAULT_VERSION;
        }

        return request.requestStartLine().version();
    }
}
