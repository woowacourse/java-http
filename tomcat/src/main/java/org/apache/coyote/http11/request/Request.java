package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.RequestHeader.COOKIE;

public class Request {

    public static final String SESSION_ID_KEY = "jsessionid";
    private final RequestLine requestLine;

    private final Headers headers;

    private final RequestParameters requestParameters;

    private final Map<String, Cookie> cookies;

    private final Session session;

    private final String body;

    private Request(final RequestLine requestLine,
                    final Headers headers,
                    final RequestParameters requestParameters,
                    final Map<String, Cookie> cookies,
                    final Session session,
                    final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestParameters = requestParameters;
        this.cookies = cookies;
        this.session = session;
        this.body = body;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaderLines = getHeader(bufferedReader);
        final RequestLine requestLine = RequestLine.from(requestHeaderLines.get(0));
        final Headers headers = new Headers();
        headers.addRequestHeaders(requestHeaderLines);

        final String body = getBody(bufferedReader, headers);

        final RequestParameters requestParameters = RequestParameters.of(requestLine, headers, body);

        final Map<String, Cookie> cookies = Cookie.getCookies(headers.getValue(COOKIE));

        final Session session = SessionManager.findSession(cookies.getOrDefault(SESSION_ID_KEY.toLowerCase(), Cookie.EMPTY_COOKIE).getValue());

        return new Request(requestLine, headers, requestParameters, cookies, session, body);
    }

    private static List<String> getHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaderLines = new ArrayList<>();
        String nextLine;
        while (!"".equals(nextLine = bufferedReader.readLine())) {
            if (nextLine == null) {
                throw new IllegalStateException("헤더가 잘못되었습니다.");
            }
            requestHeaderLines.add(nextLine);
        }
        return requestHeaderLines;
    }

    private static String getBody(final BufferedReader bufferedReader, final Headers headers) throws IOException {
        final String contentLengthValue = headers.getValue(CONTENT_LENGTH);
        final int contentLength = "".equals(contentLengthValue) ? 0 : Integer.parseInt(contentLengthValue);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean isMatching(final String requestPath, final RequestMethod requestMethod) {
        return requestLine.isMatching(requestPath, requestMethod);
    }

    public void addParameter(final String key,
                             final String value) {
        requestParameters.addParamter(key, value);
    }

    public String getParameter(final String parameterKey) {
        return requestParameters.getValue(parameterKey);
    }

    public Optional<Cookie> getCookie(final String key) {
        return Optional.ofNullable(cookies.get(key));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestParameters getRequestParameters() {
        return requestParameters;
    }

    public Session getSession() {
        return session;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", requestParameters=" + requestParameters +
                ", cookies=" + cookies +
                ", body='" + body + '\'' +
                '}';
    }
}
