package org.apache.coyote.http11.response;

import java.io.IOException;

import org.apache.coyote.http11.exception.NotCompleteResponseException;
import org.apache.coyote.http11.request.HttpHeaders;

public class Response {

    private final HttpHeaders headers;
    private final StatusLine statusLine;
    private final String content;

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    public Response(StatusLine statusLine, HttpHeaders headers, String content) {
        this.headers = headers;
        this.statusLine = statusLine;
        this.content = content;
    }

    public static class ResponseBuilder {
        private String ProtocolVersion;
        private HttpHeaders headers = new HttpHeaders();

        private ResponseBuilder() {
            ProtocolVersion = "HTTP/1.1";
        }

        public ResponseBuilder versionOf(String protocolVersion) {
            this.ProtocolVersion = protocolVersion;
            return this;
        }

        public Response found(String target) {
            this.headers.addHeader("Location", target);

            return new Response(
                    new StatusLine(this.ProtocolVersion, 301, "FOUND"),
                    this.headers,
                    null
            );
        }

        public Response ofStaticResource(StaticResource resource) throws IOException {
            headers.addHeader("Content-Type", resource.getContentType() + ";charset=utf-8");
            headers.addHeader("Content-Length", Long.toString(resource.getContentLength()));

            return new Response(
                    new StatusLine(this.ProtocolVersion, 200, "OK"),
                    this.headers,
                    resource.getContent()
            );
        }
    }

    public String toHttpMessage() {
        if (statusLine == null) {
            throw new NotCompleteResponseException("응답이 완성되지 않았습니다.");
        }
        System.out.println("statusLine.toHttpMessage() = " + statusLine.toHttpMessage());
        System.out.println("headers.toHttpMessage() = " + headers.toHttpMessage());
        return String.join("\r\n",
                statusLine.toHttpMessage(),
                headers.toHttpMessage(),
                "",
                content);
    }
}
