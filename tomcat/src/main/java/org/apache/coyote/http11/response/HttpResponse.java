package org.apache.coyote.http11.response;

import java.util.stream.Collectors;

import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.cookie.HttpCookie;

public class HttpResponse {

    private static final String ENTER = "\r\n";

    private final StatusLine statusLine;
    private final Headers headers;
    private final ResponseBody responseBody;

    private HttpResponse(StatusLine statusLine, Headers headers, ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public String getResponse() {
        String statusLineResponse =
                statusLine.getProtocolVersion() + " " +
                statusLine.getHttpStatus().getStatusCode() + " " +
                statusLine.getHttpStatus().getMessage() + " " + ENTER;
        String headerResponse = headers.getValues().entrySet()
                .stream()
                .map(header -> header.getKey() + ": " + header.getValue() + " ")
                .collect(Collectors.joining(ENTER));
        return statusLineResponse + headerResponse + ENTER + ENTER + responseBody.getValue();
    }

    public static class Builder {

        private StatusLine statusLine;
        private Headers headers = Headers.empty();
        private ResponseBody responseBody;

        public Builder statusLine(StatusLine statusLine) {
            this.statusLine = statusLine;
            return this;
        }

        public Builder redirect(String uri) {
            this.headers.add("Location", uri);
            return this;
        }

        public Builder contentType(String contentType) {
            this.headers.add("Content-Type", contentType + ";charset=utf-8");
            return this;
        }

        public Builder contentLength(int contentLength) {
            this.headers.add("Content-Length", String.valueOf(contentLength));
            return this;
        }

        public Builder setCookie(HttpCookie httpCookie) {
            this.headers.setCookie(httpCookie);
            return this;
        }

        public Builder responseBody(ResponseBody responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusLine, headers, responseBody);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
