package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.HttpCookie;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeaders headers;
    private String resource;

    public HttpResponse(){}

    public HttpResponse(StatusLine statusLine, ResponseHeaders headers, String resource) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.resource = resource;
    }

    public HttpResponse setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponse setHeaders(ResponseHeaders headers) {
        this.headers = headers;
        return this;
    }
    public HttpResponse setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public void addCookie(HttpCookie cookie) {
        String values = this.headers.getCookieValue();
        if (values == null) {
            this.headers.put("Set-Cookie", cookie.getResponse());
            return;
        }
        this.headers.put("Set-Cookie", values + " " + cookie.getResponse());
    }

    public String to() throws IOException {

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

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", resource='" + resource + '\'' +
                '}';
    }
}
