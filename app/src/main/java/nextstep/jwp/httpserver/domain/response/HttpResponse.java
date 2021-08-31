package nextstep.jwp.httpserver.domain.response;

import java.util.Map;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.HttpVersion;

public class HttpResponse {
    private StatusLine statusLine;
    private Headers headers;
    private Body body;

    public static class Builder {
        private StatusLine statusLine;
        private Headers headers;
        private Body body;

        public Builder() {
            headers = new Headers();
        }

        public Builder statusCode(StatusCode code) {
            this.statusLine = new StatusLine(code);
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.addHeader(key, value);
            return this;
        }

        public Builder body(Map<String, String> body) {
            this.body = new Body(body);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    public HttpResponse() {
        this(new StatusLine(), new Headers(), new Body());
    }

    private HttpResponse(Builder builder) {
        this(builder.statusLine, builder.headers, builder.body);
    }

    public HttpResponse(StatusLine statusLine, Headers headers, Body body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public void ok() {
        statusLine = new StatusLine(StatusCode.OK);
    }

    public void redirect(String url) {
        this.statusLine = new StatusLine(StatusCode.FOUND);
        addHeader("Location", url);
    }

    public void addHeader(String key, String value) {
        this.headers.addHeader(key, value);
    }

    public String statusLine() {
        final HttpVersion httpVersion = statusLine.getHttpVersion();
        final StatusCode statusCode = statusLine.getStatusCode();
        return httpVersion.getVersion() + " " + statusCode.getCode() + " " + statusCode.getStatusText() + " ";
    }

    public String responseToString(String responseBody) {
        return String.join("\r\n",
                statusLine(),
                headers.responseFormat(),
                "",
                responseBody
        );
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
