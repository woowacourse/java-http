package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.auth.HttpCookie;
import org.apache.coyote.http11.auth.Session;
import org.apache.coyote.http11.auth.SessionManager;
import org.apache.coyote.http11.common.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private static final int HTTP_METHOD_INDEX = 0;
    private static final String JSESSIONID_COOKIE_KEY = "JSESSIONID";
    private static final String INDEX_PAGE = "/index.html";

    private final RequestLine requestLine;
    private final HttpRequestHeader header;
    private final HttpCookie cookie;

    private HttpRequest(
            final RequestLine requestLine,
            final HttpRequestHeader header,
            final HttpCookie cookie
    ) {
        this.requestLine = requestLine;
        this.header = header;
        this.cookie = cookie;
    }

    private static HttpRequest get(
            final List<String> requestURIElements,
            final Consumer<User> logProcessor,
            final HttpRequestHeader requestHeader,
            final HttpCookie httpCookie
    ) {
        final RequestLine requestLine = RequestLine.get(requestURIElements, logProcessor);
        return new HttpRequest(requestLine, requestHeader, httpCookie);
    }

    private static HttpRequest post(
            final List<String> requestURIElements,
            final String requestBody,
            final Consumer<User> logProcessor,
            final HttpRequestHeader requestHeader,
            final HttpCookie httpCookie
    ) {
        final RequestLine requestLine = RequestLine.post(requestURIElements, requestBody, logProcessor);
        return new HttpRequest(requestLine, requestHeader, httpCookie);
    }

    public static HttpRequest of (
            final BufferedReader bufferedReader,
            final String requestLine

    ) throws IOException {
        final var requestHeader = readHeader(bufferedReader);
        final var httpCookie = HttpCookie.from(requestHeader);
        return readRequest(requestLine, requestHeader, bufferedReader, httpCookie);
    }

    private static HttpRequestHeader readHeader(final BufferedReader bufferedReader) throws IOException {
        final var stringBuilder = new StringBuilder();
        for (var line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append(CRLF);
        }

        return HttpRequestHeader.from(stringBuilder.toString());
    }

    private static HttpRequest readRequest(
            final String requestLine,
            final HttpRequestHeader httpRequestHeader,
            final BufferedReader bufferedReader,
            final HttpCookie httpCookie
    ) throws IOException {
        final var requestURIElements = parseRequestURIElements(requestLine);
        final var httpMethod = HttpMethod.valueOfHttpStatus(requestURIElements.get(HTTP_METHOD_INDEX));
        final Session session = SESSION_MANAGER.findSession(httpCookie.getValue(JSESSIONID_COOKIE_KEY));

        if (httpMethod == HttpMethod.GET) {
            return getRequest(requestURIElements, session, httpRequestHeader, httpCookie);
        }

        final String requestBody = parseRequestBody(httpRequestHeader, bufferedReader);
        return postRequest(requestURIElements, requestBody, session, httpRequestHeader, httpCookie);
    }

    private static List<String> parseRequestURIElements(final String requestURILine) {
        return Arrays.stream(requestURILine.split(SPACE))
                .collect(Collectors.toUnmodifiableList());
    }

    private static HttpRequest getRequest(
            final List<String> requestURIElements,
            final Session session,
            final HttpRequestHeader requestHeader,
            final HttpCookie httpCookie
    ) {
        if (session != null) {
            final var redirectRequestURIElements = new ArrayList<>(requestURIElements);
            redirectRequestURIElements.set(0, HttpStatus.FOUND.name());
            redirectRequestURIElements.set(1, INDEX_PAGE);
            return getRequest(redirectRequestURIElements, requestHeader, httpCookie);
        }

        return getRequest(requestURIElements, requestHeader, httpCookie);
    }

    private static HttpRequest getRequest(
            final List<String> requestURIElements,
            final HttpRequestHeader requestHeader,
            final HttpCookie httpCookie
    ) {
        return HttpRequest.get(
                requestURIElements,
                user -> log.info("user : {}", user),
                requestHeader,
                httpCookie
        );
    }

    private static String parseRequestBody(
            final HttpRequestHeader httpRequestHeader,
            final BufferedReader bufferedReader
    ) throws IOException {
        var contentLength = Integer.parseInt(httpRequestHeader.get("Content-Length"));
        var buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static HttpRequest postRequest(
            final List<String> requestURIElements,
            final String requestBody,
            final Session session,
            final HttpRequestHeader requestHeader,
            final HttpCookie cookie
    ) {
        return HttpRequest.post(
                requestURIElements,
                requestBody,
                user -> log.info("User={}", user),
                requestHeader,
                cookie
        );
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public HttpRequestHeader header() {
        return header;
    }

    public HttpCookie cookie() {
        return cookie;
    }
}
