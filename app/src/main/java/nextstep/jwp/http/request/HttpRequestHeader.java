package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpMethod;

import java.util.List;
import java.util.Map;

public class HttpRequestHeader {
    private static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    public static final int REQUEST_LINE_INDEX = 0;

    private final RequestLine requestLine;
    private final int contentLength;

    public HttpRequestHeader(final List<String> requestHeaders) {
        if (requestHeaders.isEmpty()) {
            throw new IllegalStateException();
        }

        this.requestLine = new RequestLine(requestHeaders.get(REQUEST_LINE_INDEX));
        this.contentLength = parseContentLength(requestHeaders);
    }

    private int parseContentLength(final List<String> requestHeaders) {
        final String contentLengthHeader = requestHeaders.stream()
                .filter(header -> header.startsWith(CONTENT_LENGTH_HEADER))
                .findFirst()
                .orElseGet(() -> null);

        if (contentLengthHeader == null) {
            return 0;
        }

        final String contentLengthValue = contentLengthHeader.substring(CONTENT_LENGTH_HEADER.length());
        return Integer.parseInt(contentLengthValue);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getQueryParameters() {
        return requestLine.getQueryParameters();
    }

    public int getContentLength() {
        return contentLength;
    }
}
