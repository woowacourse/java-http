package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpContentType;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.stream.Collectors;

public class HttpResponse {
    private static final String HTTP_PROTOCOL = "HTTP/1.1";

    private HttpStatus httpStatus;
    private ResponseHeaders headers;
    private String body;

    private HttpResponse() {
        this.headers = new ResponseHeaders();
        this.body = "";
    }

    public static HttpResponse init() {
        return new HttpResponse();
    }

    public void getResponse(final HttpRequest request, final String body) {
        headers.addContentType(HttpContentType.valueOfContentType(request.getExtension()).getContentType())
                .addContentLength(body);
        httpStatus = HttpStatus.OK;
        this.body = body;
    }

    public void foundResponse(final String location) {
        headers.addLocation(location);
        httpStatus = HttpStatus.FOUND;
    }

    public void addCookie(final String value) {
        this.headers.addCookie(value);
    }

    public String toResponse() {
        String responseWithoutBody = String.join(System.lineSeparator(),
                HTTP_PROTOCOL + " " + httpStatus.getCode() + " " + httpStatus.getMessage(),
                headers.getHeaders().entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining(System.lineSeparator())),
                ""
        );

        if (body.isBlank()) {
            return responseWithoutBody;
        }

        return responseWithoutBody + System.lineSeparator() + body;
    }
}
