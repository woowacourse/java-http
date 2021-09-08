package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.common.HttpMethod;
import nextstep.jwp.framework.common.HttpUri;
import nextstep.jwp.framework.common.HttpVersion;
import nextstep.jwp.framework.exception.HttpMessageConvertFailureException;
import nextstep.jwp.framework.message.StartLine;
import nextstep.jwp.utils.StringUtils;

import java.util.List;
import java.util.Objects;

public class RequestLine implements StartLine {

    private static final int REQUEST_LINE_ITEM_COUNT = 3;

    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, HttpUri httpUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.httpUri = httpUri;
        this.httpVersion = httpVersion;
    }

    private RequestLine(String httpMethod, String httpUri, String httpVersion) {
        this(HttpMethod.valueOf(httpMethod), new HttpUri(httpUri), HttpVersion.from(httpVersion));
    }

    public static RequestLine of(HttpMethod httpMethod, HttpUri httpUri, HttpVersion httpVersion) {
        return new RequestLine(httpMethod, httpUri, httpVersion);
    }

    public static RequestLine from(String requestLine) {
        List<String> words = StringUtils.splitWithSeparator(requestLine, " ");
        if (words.size() != REQUEST_LINE_ITEM_COUNT) {
            throw new HttpMessageConvertFailureException(
                    String.format("RequestLine이 적절하지 않습니다.(%s)", requestLine)
            );
        }
        return new RequestLine(words.get(0), words.get(1), words.get(2));
    }

    public String asString() {
        String httpMethodValue = this.httpMethod.name();
        String requestUri = this.httpUri.getValue();
        String httpVersionValue = this.getHttpVersion().getValue();
        return StringUtils.concatNewLine(
                StringUtils.joinWithBlank(httpMethodValue, requestUri, httpVersionValue)
        );
    }

    public String requestUri() {
        return this.httpUri.getValue();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpUri getHttpUri() {
        return httpUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    @Override
    public byte[] toBytes() {
        return asString().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLine that = (RequestLine) o;
        return getHttpMethod() == that.getHttpMethod() && Objects.equals(getHttpUri(), that.getHttpUri()) && getHttpVersion() == that.getHttpVersion();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHttpMethod(), getHttpUri(), getHttpVersion());
    }
}
