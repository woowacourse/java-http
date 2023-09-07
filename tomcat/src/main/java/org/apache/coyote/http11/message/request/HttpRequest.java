package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.message.Cookie;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final QueryParams queryParams;
    private final RequestBody body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers,
        final QueryParams queryParams, final RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader messageReader) throws IOException {
        final String startLine = messageReader.readLine();
        final RequestLine requestLine = RequestLine.from(startLine);
        final QueryParams queryParams = QueryParams.from(requestLine);
        final HttpHeaders httpHeaders = parseHeaders(messageReader);
        final RequestBody requestBody = readBody(messageReader, httpHeaders);

        return new HttpRequest(requestLine, httpHeaders, queryParams, requestBody);
    }

    private static HttpHeaders parseHeaders(final BufferedReader messageReader) throws IOException {
        String readLine;
        final List<String> readHeaderLines = new ArrayList<>();
        while ((readLine = messageReader.readLine()) != null && !readLine.isEmpty()) {
            readHeaderLines.add(readLine);
        }

        return HttpHeaders.from(readHeaderLines);
    }

    private static RequestBody readBody(final BufferedReader reader, final HttpHeaders httpHeaders)
        throws IOException {
        final Optional<String> contentLengthValue = httpHeaders.findFirstValueOfField(CONTENT_LENGTH_HEADER);
        if (contentLengthValue.isEmpty()) {
            return RequestBody.empty();
        }
        final int contentLength = Integer.parseInt(contentLengthValue.get());

        final char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    public boolean isRequestOf(final HttpMethod method, final String path) {
        return requestLine.isMatchingRequest(method, path);
    }

    public Optional<String> findFirstHeaderValue(final String field) {
        return headers.findFirstValueOfField(field);
    }

    public Session getSession(final boolean createIfNotExist) {
        final Optional<Cookie> foundCookie = getCookie();
        final String sessionId = foundCookie.map(cookie -> cookie.findByName("JSESSIONID"))
            .orElse(null);

        if (sessionId != null) {
            return SessionManager.findSession(sessionId);
        }
        if (createIfNotExist) {
            final Session session = new Session(UUID.randomUUID().toString());
            SessionManager.add(session);
            return session;
        }
        return null;
    }

    private Optional<Cookie> getCookie() {
        final Optional<String> cookiesInHeaderLine = headers.getValuesOfField(COOKIE_HEADER);
        return cookiesInHeaderLine.map(Cookie::fromHeaderCookie);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParamOf(final String field) {
        return queryParams.getValueOf(field);
    }

    public String getBodyOf(final String key) {
        return body.getValueOf(key);
    }

    public Map<String, String> getRequestBody() {
        return body.getFieldsWithValue();
    }
}
