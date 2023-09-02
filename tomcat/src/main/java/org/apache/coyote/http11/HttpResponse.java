package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {

    private final StatusCode statusCode;
    private final String request;

    public HttpResponse(final StatusCode statusCode, final String request) {
        this.statusCode = statusCode;
        this.request = request;
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType() + ";charset=utf-8 ",
                "Content-Length: " + request.getBytes().length + " ",
                "",
                request);
    }

    private String contentType(){

        final String[] requestLines = request.split("\\s+");
        System.out.println("request111: "+request);
        System.out.println("출력: "+requestLines[1]);
        return ContentType.from(requestLines[1]).getContentType();
    }
}
