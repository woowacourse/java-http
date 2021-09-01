package nextstep.jwp.web.http.response;

import static java.util.stream.Collectors.joining;

import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.session.HttpCookie;

public class HttpResponseImpl implements HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String COLON_AND_BLANK = ": ";
    private static final String BLANK = " ";
    private static final String FORMAT = "%s %s %s \r\n" +
        "%s\r\n" +
        "\r\n" +
        "%s";

    private final HttpRequest request;
    private final HttpProtocol protocol;
    private HttpStatus status;
    private final HttpHeaders headers;
    private ContentType contentType;
    private String body;

    public HttpResponseImpl(HttpRequest request,
                            HttpProtocol protocol,
                            HttpStatus status,
                            HttpHeaders headers,
                            ContentType contentType,
                            String body) {
        this.request = request;
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.contentType = contentType;
        this.body = body;
    }

    @Override
    public boolean isSuitableContentType(ContentType type) {
        return contentType == type;
    }

    @Override
    public HttpRequest request() {
        return this.request;
    }

    @Override
    public HttpHeaders headers() {
        return this.headers;
    }

    @Override
    public HttpProtocol protocol() {
        return this.protocol;
    }

    @Override
    public HttpStatus status() {
        return this.status;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public void setContentType(ContentType contentType) {
        this.headers.setContentType(contentType);
        this.contentType = contentType;
    }

    @Override
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void setCookie(HttpCookie cookie) {
        this.headers.setResponseCookie(cookie);
    }

    @Override
    public String toString() {
        final String formattedHeaders = formatHeaderString();

        return String.format(FORMAT,
            protocol.getProtocolName(), status.getCode(), status.getMessage(),
            formattedHeaders,
            body
        );
    }

    private String formatHeaderString() {
        return this.headers().map().entrySet().stream()
            .map(set -> set.getKey() + COLON_AND_BLANK + set.getValue().toValuesString() + BLANK)
            .collect(joining(DELIMITER));
    }

    public static class Builder {

        private final HttpRequest request;
        private final HttpProtocol protocol;
        private final HttpStatus status;
        private final HttpHeaders headers = new HttpHeaders();
        private ContentType contentType;
        private String body;

        public Builder(HttpRequest request, HttpStatus status) {
            this.request = request;
            this.protocol = request.protocol();
            this.status = status;
        }

        public Builder setHeaders(HttpHeaders headers) {
            this.headers.addAll(headers);
            return this;
        }

        public Builder setResponseBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setContentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpResponseImpl build() {
            return new HttpResponseImpl(this.request, this.protocol, this.status,
                this.headers, this.contentType, this.body);
        }

    }
}
