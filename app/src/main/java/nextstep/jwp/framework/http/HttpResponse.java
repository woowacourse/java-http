package nextstep.jwp.framework.http;

import java.util.Objects;

import nextstep.jwp.framework.http.formatter.LineFormatter;
import nextstep.jwp.framework.http.formatter.StatusLineFormatter;

public class HttpResponse implements HttpMessage {

    private static final String EMPTY = "";

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;
    private final int contentLength;
    private final String responseBody;

    public HttpResponse(StatusLine statusLine, HttpHeaders httpHeaders, int contentLength, String responseBody) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.contentLength = contentLength;
        this.responseBody = Objects.requireNonNullElse(responseBody, EMPTY);
    }

    public static Builder ok() {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        return new Builder(statusLine).header(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
    }

    public static Builder notFound() {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.NOT_FOUND);
        return new Builder(statusLine).header(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
    }

    public static Builder found(String location) {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.FOUND);
        return new Builder(statusLine).header(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8")
                                      .header(HttpHeader.LOCATION, location);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getBody() {
        return responseBody;
    }

    public String readAsString() {
        return readAsString(this);
    }

    public String readAsString(HttpResponse httpResponse) {
        LineFormatter lineFormatter = new StatusLineFormatter(httpResponse);
        StringBuilder stringBuilder = new StringBuilder();
        while (lineFormatter.canRead()) {
            stringBuilder.append(lineFormatter.transform());
            lineFormatter = lineFormatter.convertNextFormatter();
        }
        return stringBuilder.toString();
    }

    public String readAfterExceptBody() {
        final HttpResponse emptyBodyResponse = new Builder().copy(this)
                                                            .body(EMPTY)
                                                            .build();

        return readAsString(emptyBodyResponse);
    }

    public static class Builder {
        private StatusLine statusLine;
        private HttpHeaders httpHeaders = new HttpHeaders();
        private int contentLength;
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

        public Builder header(String headerName, String... values) {
            this.httpHeaders.addHeader(headerName, values);
            return this;
        }

        public Builder contentLength(int contentLength) {
            this.httpHeaders.addHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(contentLength));
            this.contentLength = contentLength;
            return this;
        }

        public Builder body(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder copy(HttpResponse httpResponse) {
            statusLine(new StatusLine(httpResponse.statusLine));
            httpHeaders(new HttpHeaders(httpResponse.httpHeaders));
            contentLength(httpResponse.contentLength);
            body(httpResponse.responseBody);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusLine, httpHeaders, contentLength, responseBody);
        }
    }
}
