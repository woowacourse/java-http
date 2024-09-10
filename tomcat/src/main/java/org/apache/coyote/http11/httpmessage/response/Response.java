package org.apache.coyote.http11.httpmessage.response;

import java.io.IOException;

import org.apache.coyote.http11.exception.NotCompleteResponseException;
import org.apache.coyote.http11.httpmessage.HttpCookie;
import org.apache.coyote.http11.httpmessage.HttpHeaders;

public class Response {

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private final String content;

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    public Response(StatusLine statusLine, HttpHeaders headers, String content) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.content = content;
    }

    public static class ResponseBuilder {
        private String ProtocolVersion;
        private HttpCookie httpCookie;
        private HttpHeaders headers = new HttpHeaders();

        private ResponseBuilder() {
            ProtocolVersion = "HTTP/1.1";
            httpCookie = new HttpCookie();
        }

        public ResponseBuilder versionOf(String protocolVersion) {
            this.ProtocolVersion = protocolVersion;
            return this;
        }

        public ResponseBuilder addCookie(String key, String value) {
            httpCookie.addCookie(key, value);
            return this;
        }

        public Response found(String target) {
            this.headers.addHeader(HttpHeaders.LOCATION, target);

            return build(
                    new StatusLine(this.ProtocolVersion, 301, "FOUND"),
                    this.headers,
                    ""
            );
        }

        public Response ofStaticResource(StaticResource resource) throws IOException {
            headers.addHeader(HttpHeaders.CONTENT_TYPE, resource.getContentType() + ";charset=utf-8");
            headers.addHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(resource.getContentLength()));

            return build(
                    new StatusLine(this.ProtocolVersion, 200, "OK"),
                    this.headers,
                    resource.getContent()
            );
        }

        public Response build(StatusLine statusLine, HttpHeaders headers, String content) {
            setCookie(headers);
            return new Response(statusLine, headers, content);
        }

        private void setCookie(HttpHeaders headers) {
            if(!httpCookie.isEmpty()) {
                headers.addHeader(HttpHeaders.SET_COOKIE, httpCookie.toHttpMessage());
            }
        }
    }

    public String toHttpMessage() {
        if (statusLine == null) {
            throw new NotCompleteResponseException("응답이 완성되지 않았습니다.");
        }
        return String.join("\r\n",
                statusLine.toHttpMessage(),
                headers.toHttpMessage(),
                "",
                content);
    }
}
