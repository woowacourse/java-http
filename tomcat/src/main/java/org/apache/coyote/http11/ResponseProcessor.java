package org.apache.coyote.http11;

import com.http.enums.HttpStatus;
import org.apache.catalina.connector.ResponseHeaderUtil;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.domain.ResponseStartLine;

public final class ResponseProcessor {

    private ResponseProcessor() {
    }

    public static void handle(HttpRequest request, HttpResponse response, HttpStatus httpStatus) {
        final String version = request.requestStartLine().version();

        response.setStartLine(new ResponseStartLine(version, httpStatus));

        ResponseHeaderUtil.handle(request, response);
    }

    public static void handleBadRequest(HttpResponse response) {
        response.setStartLine(new ResponseStartLine("HTTP/1.1", HttpStatus.BAD_REQUEST));
        response.addHeader("Connection", "close");
    }
}
