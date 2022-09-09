package nextstep.jwp.http;

import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;

public class Response {

    private static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP11;
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.OK;
    private static final String DEFAULT_CONTENT = "";

    private HttpVersion httpVersion = DEFAULT_HTTP_VERSION;
    private HttpStatus httpStatus = DEFAULT_HTTP_STATUS;
    private final Headers headers = new Headers();
    private String content = DEFAULT_CONTENT;

    public Response httpVersion(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public Response httpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public Response header(final HttpHeader key, final String value) {
        headers.put(key, value);
        return this;
    }

    public Response content(final String content) {
        this.content = content;
        this.headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        return this;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String parse() {
        return String.join(
                "\r\n",
                String.format("%s %s %s ", httpVersion.getValue(), httpStatus.getCode(), httpStatus.name()),
                headers.parse(),
                content
        );
    }
}
