package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private String httpStatus;
    private String contentType;
    private String body;
    private Map<String, String> headers;

    public HttpResponse(final String httpStatus, final String contentType, final String body) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
    }

    public HttpResponse(final String httpStatus, final String contentType, final String body,
                        final Map<String, String> headers) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
        this.headers = headers;
    }

    private String joinHeaders() {
        if (headers == null) {
            return "";
        }
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining());
    }

    private String joinBodyMetadata() {
        if (body == null) {
            return "";
        }
        return String.join("\r\n",
                contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    public String buildResponse() {
        String headers = joinHeaders();
        String body = joinBodyMetadata();

        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus,
                headers + " ",
                body);
    }

}
