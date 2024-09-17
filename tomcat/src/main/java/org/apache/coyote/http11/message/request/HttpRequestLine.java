package org.apache.coyote.http11.message.request;

import java.util.Optional;

import org.apache.coyote.http11.FileExtensionType;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpVersion;

public class  HttpRequestLine {

    private static final String HTTP_REQUEST_LINE_FIELD_SEPARATOR = " ";
    private static final int HTTP_REQUEST_LINE_FIELDS_COUNT = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final HttpRequestUri httpRequestUri;
    private final HttpVersion httpVersion;

    public HttpRequestLine(final String httpRequestLine) {
        validateHttpRequestLineIsNullOrBlank(httpRequestLine);
        validateHttpRequestLineFieldsCount(httpRequestLine);
        this.httpMethod = parseHttpMethod(httpRequestLine);
        this.httpRequestUri = parseHttpRequestUri(httpRequestLine);
        this.httpVersion = parseHttpVersion(httpRequestLine);
    }

    private void validateHttpRequestLineIsNullOrBlank(final String httpRequestLine) {
        if (httpRequestLine == null || httpRequestLine.isBlank()) {
            throw new IllegalArgumentException("Http Request Line은 null 혹은 빈 값이 입력될 수 없습니다. - " + httpRequestLine);
        }
    }

    private void validateHttpRequestLineFieldsCount(final String httpRequestLine) {
        final String[] httpRequestLineFields = parseHttpRequestLineFields(httpRequestLine);
        if (httpRequestLineFields.length != HTTP_REQUEST_LINE_FIELDS_COUNT) {
            throw new IllegalArgumentException("유효하지 않은 HTTP Request Line 필드 개수입니다. - " + httpRequestLine);
        }
    }

    private String[] parseHttpRequestLineFields(final String httpRequestLine) {
        return httpRequestLine.split(HTTP_REQUEST_LINE_FIELD_SEPARATOR);
    }

    private HttpMethod parseHttpMethod(final String httpRequestLine) {
        final String[] httpRequestLineFields = parseHttpRequestLineFields(httpRequestLine);
        return HttpMethod.getByValue(httpRequestLineFields[HTTP_METHOD_INDEX]);
    }

    private HttpRequestUri parseHttpRequestUri(final String httpRequestLine) {
        final String[] httpRequestLineFields = parseHttpRequestLineFields(httpRequestLine);
        return new HttpRequestUri(httpRequestLineFields[HTTP_REQUEST_URI_INDEX]);
    }

    private HttpVersion parseHttpVersion(final String httpRequestLine) {
        final String[] httpRequestLineFields = parseHttpRequestLineFields(httpRequestLine);
        return HttpVersion.getByValue(httpRequestLineFields[HTTP_VERSION_INDEX]);
    }

    public boolean matchRequestUri(final String uri) {
        return httpRequestUri.matchRequestUri(uri);
    }

    public boolean matchHttpMethod(final HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public boolean isStaticResourceUri() {
        return httpRequestUri.isStaticResourceUri();
    }

    public HttpRequestUri getHttpRequestUri() {
        return httpRequestUri;
    }

    public Optional<FileExtensionType> getFileExtensionType() {
        return httpRequestUri.getFileExtensionType();
    }
}
