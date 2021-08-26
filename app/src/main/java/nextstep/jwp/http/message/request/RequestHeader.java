package nextstep.jwp.http.message.request;

import nextstep.jwp.exception.HttpMessageConvertFailureException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.message.HeaderFields;
import nextstep.jwp.http.message.MessageHeader;
import nextstep.jwp.utils.StringUtils;

import java.util.List;
import java.util.Objects;

public class RequestHeader implements MessageHeader {

    private static final String BLANK = " ";
    private static final String LINE_SEPARATOR = "\r\n";

    private final RequestLine requestLine;
    private final HeaderFields headerFields;

    private RequestHeader(String requestLine, String headerFieldLines) {
        this(RequestLine.from(requestLine), HeaderFields.from(headerFieldLines));
    }

    private RequestHeader(RequestLine requestLine, HeaderFields headerFields) {
        this.requestLine = requestLine;
        this.headerFields = headerFields;
    }

    public static RequestHeader from(String headerString) {
        List<String> lines = StringUtils.splitTwoPiecesWithSeparator(headerString, LINE_SEPARATOR);
        String requestLine = lines.get(0);
        String headerFieldLines = lines.get(1);
        return new RequestHeader(requestLine, headerFieldLines);
    }

    public byte[] toBytes() {
        return convertToString().getBytes();
    }

    public String convertToString() {
        return requestLine() + headerFields.asString();
    }

    private String requestLine() {
        return String.join(BLANK,
                requestLine.httpMethod,
                requestUri(),
                httpVersion(),
                LINE_SEPARATOR);
    }

    public void changeRequestUri(String requestUri) {
        requestLine.requestUri = requestUri;
    }

    public HttpMethod httpMethod() {
        return HttpMethod.valueOf(requestLine.httpMethod);
    }

    public String requestUri() {
        return requestLine.requestUri;
    }

    public String httpVersion() {
        return requestLine.httpVersion;
    }

    @Override
    public HeaderFields getHeaderFields() {
        return this.headerFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestHeader that = (RequestHeader) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(getHeaderFields(), that.getHeaderFields());
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, getHeaderFields());
    }

    private static class RequestLine {

        private static final int REQUEST_LINE_ITEM_COUNT = 3;

        private final String httpMethod;
        private String requestUri;
        private final String httpVersion;

        private RequestLine(String httpMethod, String requestUri, String httpVersion) {
            this.httpMethod = httpMethod;
            this.requestUri = requestUri;
            this.httpVersion = httpVersion;
        }

        private static RequestLine from(String requestLine) {
            List<String> words = StringUtils.splitWithSeparator(requestLine, " ");
            if (words.size() != REQUEST_LINE_ITEM_COUNT) {
                throw new HttpMessageConvertFailureException(
                        String.format("RequestLine이 적절하지 않습니다.(%s)", requestLine)
                );
            }
            return new RequestLine(words.get(0), words.get(1), words.get(2));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RequestLine that = (RequestLine) o;
            return Objects.equals(httpMethod, that.httpMethod) && Objects.equals(requestUri, that.requestUri) && Objects.equals(httpVersion, that.httpVersion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(httpMethod, requestUri, httpVersion);
        }
    }
}
