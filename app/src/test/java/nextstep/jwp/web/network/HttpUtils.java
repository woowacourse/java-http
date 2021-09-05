package nextstep.jwp.web.network;

import nextstep.jwp.web.network.request.HttpMethod;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.ContentType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.StringJoiner;
import java.util.UUID;

public class HttpUtils {

    public static class RequestBuilder {
        private final String method;
        private final String uri;
        private final String protocolVersion;
        private String host;
        private String connection;
        private String cookie;
        private String contentLength;
        private String contentType;
        private String accept;
        private String body;

        private RequestBuilder(String method, String uri, String protocolVersion) {
            this.method = method;
            this.uri = uri;
            this.protocolVersion = protocolVersion;
        }

        public static RequestBuilder builder(HttpMethod method, String uri) {
            return builder(method.name(), uri, "HTTP/1.1");
        }

        public static RequestBuilder builder(String method, String uri, String protocolVersion) {
            return new RequestBuilder(method, uri, protocolVersion);
        }

        public RequestBuilder host(String host) {
            this.host = String.format("Host: %s ", host);
            return this;
        }

        public RequestBuilder localHost8080() {
            this.host = "Host: localhost:8080 ";
            return this;
        }

        public RequestBuilder connection(String connection) {
            this.connection = String.format("Connection: %s ", connection);
            return this;
        }

        public RequestBuilder connectionKeepAlive() {
            this.connection = "Connection: keep-alive ";
            return this;
        }

        public RequestBuilder cookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

        public RequestBuilder jsessionid(UUID id) {
            this.cookie = String.format("Cookie: JSESSIONID=%s", id.toString());
            return this;
        }

        public RequestBuilder contentLength(int length) {
            this.contentLength = String.format("Content-Length: %d ", length);
            return this;
        }

        public RequestBuilder contentType(ContentType type) {
            this.contentType = String.format("Content-Type: %s ", type.getType());
            return this;
        }

        public RequestBuilder accept(String accept) {
            this.accept = String.format("Accept: %s ", accept);
            return this;
        }

        public RequestBuilder acceptAll() {
            this.accept = "Accept: */* ";
            return this;
        }

        public RequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public InputStream buildInputStream() {
            final StringJoiner stringJoiner = new StringJoiner("\r\n");
            includeMandatory(stringJoiner);
            includeCookie(stringJoiner);
            includeContentInfo(stringJoiner);
            stringJoiner.add("");
            includeBody(stringJoiner);
            stringJoiner.add("");
            return new ByteArrayInputStream(stringJoiner.toString().getBytes());
        }

        private void includeMandatory(StringJoiner stringJoiner) {
            final String requestLine = String.format("%s %s %s ", method, uri, protocolVersion);
            stringJoiner
                    .add(requestLine)
                    .add(host)
                    .add(connection)
                    .add(accept);
        }

        private void includeCookie(StringJoiner stringJoiner) {
            if (cookie != null) {
                stringJoiner.add(cookie);
            }
        }

        private void includeContentInfo(StringJoiner stringJoiner) {
            if ("POST".equals(this.method)) {
                stringJoiner
                        .add(contentType)
                        .add(contentLength);
            }
        }

        private void includeBody(StringJoiner stringJoiner) {
            if (body != null) {
                stringJoiner.add(body);
            }
        }

        public HttpRequest buildRequest() {
            return new HttpRequest(buildInputStream());
        }
    }
}
