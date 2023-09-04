package org.apache.coyote.http11;

import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.join;

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

    private String getHeaders() {
        String joined = joinHeaders();
        if (body == null) {
            return joined;
        }
        if (joined.isBlank()) {
            return contentType + "\r\n" + "Content-Length: " + body.getBytes().length + " ";
        }
        return join("\r\n",
                joined,
                contentType,
                "Content-Length: " + body.getBytes().length + " ");
    }

    private String joinHeaders() {
        String joined = "";
        if (headers != null) {
            joined = headers.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                    .collect(Collectors.joining("\r\n"));
        }
        return joined;
    }

    private String getBody() {
        if (body == null) {
            return "";
        }
        return "\r\n" + body;
    }

    public String buildResponse() {
        String joinedHeader = getHeaders();

        String startLine = "HTTP/1.1 " + httpStatus + " ";
        String withHeader = join("\r\n", startLine, joinedHeader);

        return join("\r\n", withHeader, getBody());
    }

}
