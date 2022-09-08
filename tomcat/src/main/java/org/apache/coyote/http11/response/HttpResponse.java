package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.HttpCookie;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final String resource;

    public HttpResponse(StatusLine statusLine, ResponseHeaders headers, String resource) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.resource = resource;
    }

    public HttpResponse addCookie(HttpCookie cookie) {
        this.headers.put("Set-Cookie", cookie.getResponse());
        return this;
    }

    public String toResponse() throws IOException {

        String statusLineResponse = statusLine.getResponse();
        String headersResponse = headers.getHeader().entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));

        return statusLineResponse + headersResponse + "\r\n" + "\r\n" + resource;
    }

    public String getResource() {
        return resource;
    }
}
