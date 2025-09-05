package org.apache.coyote.http11;

import com.http.enums.HttpStatus;
import java.io.IOException;
import org.apache.catalina.connector.ResponseHeaderUtil;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.domain.ResponseStartLine;
import org.apache.catalina.util.FileParser;

public final class ResponseProcessor {

    private ResponseProcessor() {
    }

    public static void handle(HttpRequest request, HttpResponse response, HttpStatus httpStatus) {
        final String version = request.requestStartLine().version();

        response.setStartLine(new ResponseStartLine(version, httpStatus));

        ResponseHeaderUtil.handle(request, response);
    }

    public static void handleBadRequest(HttpResponse response) throws IOException {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        handleErrorPage(response, badRequest);
    }

    public static void handleErrorPage(HttpResponse response, HttpStatus httpStatus) throws IOException {
        response.setStartLine(new ResponseStartLine("HTTP/1.1", httpStatus));
        response.addHeader("Connection", "close");

        final byte[] bytes = FileParser.loadErrorPage(httpStatus);
        response.setBody(bytes);
    }
}
