package nextstep.jwp.web.http.response;

import static java.util.stream.Collectors.joining;

import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.response.body.HttpResponseBody;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String COLON_AND_BLANK = ": ";
    private static final String BLANK = " ";
    private static final String FORMAT = "%s %s %s \r\n" +
        "%s\r\n" +
        "\r\n" +
        "%s";

    private final HttpProtocol protocol;
    private HttpStatus status;
    private final HttpHeaders headers;
    private HttpResponseBody responseBody;

    public HttpResponse(HttpProtocol protocol, HttpStatus status, HttpHeaders headers,
                        HttpResponseBody responseBody) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(HttpProtocol protocol, HttpResponseBody responseBody) {
        return new Builder(protocol, HttpStatus.OK)
                    .setResponseBody(responseBody)
                    .build();
    }

    public String toResponseFormat() {
        final String formattedHeaders = formatHeaderString();

        return String.format(FORMAT,
            protocolName(), statusCode(), statusName(),
            formattedHeaders,
            responseBody().body()
        );
    }

    public HttpProtocol protocol() {
        return protocol;
    }

    public HttpStatus status() {
        return status;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public HttpResponseBody responseBody() {
        return responseBody;
    }

    public String protocolName() {
        return protocol.getProtocolName();
    }

    public int statusCode() {
        return status.getCode();
    }

    public String statusName() {
        return status.getMessage();
    }

    public void replaceResponseBody(HttpResponseBody responseBody) {
        ContentType contentType = ContentType.findByFileType(responseBody.fileType());
        this.headers.set("Content-Type", contentType.getText());
        this.headers.set("Content-Length", String.valueOf(responseBody.body().getBytes().length));
        this.responseBody = responseBody;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    private String formatHeaderString() {
        return this.headers().map().entrySet().stream()
            .map(set -> set.getKey() + COLON_AND_BLANK + set.getValue().toValuesString() + BLANK)
            .collect(joining(DELIMITER));
    }

    public static class Builder {

        private HttpProtocol protocol;
        private HttpStatus status;
        private final HttpHeaders headers = new HttpHeaders();
        private HttpResponseBody responseBody = new TextHttpResponseBody("", FileType.HTML);

        public Builder(HttpProtocol protocol, HttpStatus status) {
            this.protocol = protocol;
            this.status = status;
        }

        public Builder setHeaders(HttpHeaders headers) {
            this.headers.addAll(headers);
            return this;
        }

        public Builder setResponseBody(HttpResponseBody responseBody) {
            ContentType contentType = ContentType.findByFileType(responseBody.fileType());
            this.headers.set("Content-Type", contentType.getText());
            this.headers.set("Content-Length", String.valueOf(responseBody.body().getBytes().length));
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this.protocol, this.status,
                                    this.headers, this.responseBody);
        }

    }
}
