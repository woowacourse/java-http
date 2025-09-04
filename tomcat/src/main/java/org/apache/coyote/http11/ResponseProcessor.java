package org.apache.coyote.http11;

import org.apache.catalina.connector.ResponseHeaderUtil;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;

public class ResponseProcessor {

    public static void handle(HttpRequest request, HttpResponse response) {
        final String startLine = "HTTP/1.1 200 OK ";

        response.setStartLine(startLine);

        ResponseHeaderUtil.handle(request, response);
    }
}
