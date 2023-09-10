package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.exception.NotFoundQueryStringException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

import static org.apache.coyote.http11.request.RequestBody.EMPTY_REQUEST_BODY;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Cookies cookies;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders, final Cookies cookies, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.cookies = cookies;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestRequestLine = RequestLine.from(bufferedReader.readLine());
        final RequestHeaders requestHeaders = getHeaders(bufferedReader);
        final Cookies requestCookies = getCookies(requestHeaders);
        final RequestBody requestBody = getBody(requestHeaders, bufferedReader);

        return new HttpRequest(requestRequestLine, requestHeaders, requestCookies, requestBody);
    }

    private static RequestHeaders getHeaders(final BufferedReader bufferedReader) {
        return RequestHeaders.from(
                bufferedReader.lines()
                              .takeWhile(line -> !line.isEmpty())
        );
    }

    private static Cookies getCookies(final RequestHeaders requestHeaders) {
        return requestHeaders.getHeaders().stream()
                             .filter(requestHeader -> requestHeader.getName().equals("Cookie"))
                             .findFirst()
                             .map(requestHeader -> Cookies.from(requestHeader.getValue()))
                             .orElse(Cookies.EMPTY_COOKIES);

    }

    private static RequestBody getBody(final RequestHeaders requestHeaders, final BufferedReader bufferedReader) throws IOException {
        final Optional<RequestHeader> contentLengthHeader = requestHeaders.getHeaders()
                                                                          .stream()
                                                                          .filter(requestHeader -> requestHeader.getName().equals(CONTENT_LENGTH))
                                                                          .findFirst();

        if (contentLengthHeader.isPresent()) {
            return generateBody(bufferedReader, contentLengthHeader.get());
        }

        return EMPTY_REQUEST_BODY;
    }

    private static RequestBody generateBody(final BufferedReader bufferedReader, final RequestHeader contentLengthRequestHeader) throws IOException {
        final int contentLength = Integer.parseInt(contentLengthRequestHeader.getValue());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return RequestBody.from(new String(buffer));
    }

    public boolean isSameMethod(final Method method) {
        return this.requestLine.isSameMethod(method);
    }

    public boolean isSameUri(final String uri) {
        return this.requestLine.isSameUri(uri);
    }

    public boolean hasHeaderBy(final String name) {
        return requestHeaders.getHeaders().stream()
                             .anyMatch(requestHeader -> requestHeader.getName().equals(name));
    }

    public boolean hasQueryString() {
        return requestLine.getQueryString().isPresent();
    }

    public String getPath() {
        return requestLine.getUri();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getQueryValue(final String key) {
        final QueryString queryString = requestLine.getQueryString()
                                                   .orElseThrow(() ->
                                                         new NotFoundQueryStringException("쿼리 스트링이 들어오지 않았습니다.")
                                                 );
        return queryString.getQueryValue(key);
    }

    public boolean hasCookieKey(final String key) {
        return cookies.getCookies()
                      .containsKey(key);
    }

    public String getCookieValue(final String key) {
        return cookies.getValueBy(key);
    }

    public String getBodyValue(final String key) {
        return requestBody.getValues().get(key);
    }

    public RequestHeaders getHeaders() {
        return requestHeaders;
    }
}
