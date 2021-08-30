package nextstep.jwp.httpserver.domain.response;

import java.util.Map;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.HttpVersion;
import nextstep.jwp.httpserver.domain.StatusCode;

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
        headers = new Headers();
    }

    private HttpResponse(Builder builder) {
        this.statusLine = builder.statusLine;
        this.headers = builder.headers;
        this.body = builder.body;
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

    public String defaultErrorRedirectResponse(StatusCode code, String responseBody) {
        headers.addHeader("Location", "/" + code.getCode() + ".html");
        return String.join("\r\n",
                "HTTP/1.1 " + StatusCode.FOUND.getCode() + " " + StatusCode.FOUND.getStatusText() + " ",
                headers.responseFormat(),
                "",
                responseBody);
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
