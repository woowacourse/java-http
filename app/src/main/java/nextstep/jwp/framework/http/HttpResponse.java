package nextstep.jwp.framework.http;

import java.util.Objects;

import nextstep.jwp.framework.http.formatter.HttpFormatter;
import nextstep.jwp.framework.http.formatter.StatusLineFormatter;

public class HttpResponse implements HttpMessage {

    private static final String EMPTY = "";

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;
    private final String responseBody;

    public HttpResponse(StatusLine statusLine, HttpHeaders httpHeaders, String responseBody) {
        this.statusLine = Objects.requireNonNull(statusLine);
        this.httpHeaders = Objects.requireNonNull(httpHeaders);
        this.responseBody = Objects.requireNonNullElse(responseBody, EMPTY);
    }

    public static Builder status(HttpStatus httpStatus) {
        return new Builder(new StatusLine(HttpVersion.HTTP_1_1, httpStatus));
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getBody() {
        return responseBody;
    }

    public String readAsString() {
        return readAsString(this);
    }

    public String readAsString(HttpResponse httpResponse) {
        HttpFormatter httpFormatter = new StatusLineFormatter(httpResponse.statusLine);
        StringBuilder stringBuilder = new StringBuilder();
        while (httpFormatter.canRead()) {
            stringBuilder.append(httpFormatter.transform());
            httpFormatter = httpFormatter.convertNextFormatter(httpResponse);
        }
        return stringBuilder.toString();
    }

    public String readAfterExceptBody() {
        final HttpResponse emptyBodyResponse = new Builder().copy(this)
                                                            .overwrite(EMPTY)
                                                            .build();

        return readAsString(emptyBodyResponse);
    }

    public static class Builder {
        private StatusLine statusLine;
        private HttpHeaders httpHeaders = new HttpHeaders();
        private String responseBody;

        public Builder() {}

        public Builder(StatusLine statusLine) {
            this.statusLine = statusLine;
        }

        public Builder statusLine(StatusLine statusLine) {
            this.statusLine = statusLine;
            return this;
        }

        public Builder httpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder header(String headerName, String value) {
            this.httpHeaders.addHeader(headerName, value);
            return this;
        }

        public Builder contentLength(int contentLength) {
            this.httpHeaders.addHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(contentLength));
            return this;
        }

        public Builder contentType(String contentType) {
            this.httpHeaders.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            return this;
        }

        public Builder location(String location) {
            this.httpHeaders.addHeader(HttpHeaders.LOCATION, location);
            return this;
        }

        public Builder body(String responseBody) {
            this.responseBody = responseBody;
            if (!responseBody.isBlank()) {
                contentLength(responseBody.getBytes().length);
            }
            return this;
        }

        private Builder overwrite(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder copy(HttpResponse httpResponse) {
            statusLine(new StatusLine(httpResponse.statusLine));
            httpHeaders(new HttpHeaders(httpResponse.httpHeaders));
            overwrite(httpResponse.responseBody);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusLine, httpHeaders, responseBody);
        }
    }
}
