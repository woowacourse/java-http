package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.EntityHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.header.RequestHeader.COOKIE;

public class Request {

    private final RequestLine requestLine;

    private final Headers headers;

    private final RequestParameters requestParameters;

    private final HttpCookie httpCookie;

    private final Session session;

    private final String body;

    public Request(final RequestLine requestLine,
                   final Headers headers,
                   final RequestParameters requestParameters,
                   final HttpCookie httpCookie,
                   final Session session,
                   final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestParameters = requestParameters;
        this.httpCookie = httpCookie;
        this.session = session;
        this.body = body;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaderLines = getHeader(bufferedReader);

        final String requestFirstLine = requestHeaderLines.get(0);
        final RequestLine requestLine = RequestLine.from(requestFirstLine);
        final Headers headers = new Headers();
        headers.addRequestHeaders(requestHeaderLines);

        final String body = getBody(bufferedReader, headers);

        final String queryStrings = extractQueryStrings(requestFirstLine, body, headers);
        final RequestParameters requestParameters = RequestParameters.from(queryStrings);

        final String cookieHeader = headers.getValue(COOKIE);
        final HttpCookie httpCookie = HttpCookie.from(cookieHeader);

        final String jsessionid = httpCookie.findCookie("JSESSIONID");
        final Session session = SessionManager.findSession(jsessionid);

        return new Request(requestLine, headers, requestParameters, httpCookie, session, body);
    }

    private static String getBody(final BufferedReader bufferedReader, final Headers headers) throws IOException {
        final String contentLengthValue = headers.getValue(CONTENT_LENGTH) ;
        final int contentLength = "".equals(contentLengthValue) ? 0 : Integer.parseInt(contentLengthValue);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static List<String> getHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaderLines = new ArrayList<>();
        String nextLine;
        while (!"".equals(nextLine = bufferedReader.readLine())) {
            if (nextLine == null) {
                throw new RuntimeException("헤더가 잘못되었습니다.");
            }
            requestHeaderLines.add(nextLine);
        }
        return requestHeaderLines;
    }

    private static String extractQueryStrings(final String requestFirstLine,
                                              final String body,
                                              final Headers headers) {

        final String[] requestFirstLineElements = requestFirstLine.split(" ");
        final String requestUriValue = requestFirstLineElements[1];

        if (requestUriValue.contains("?")) {
            return requestUriValue.substring(requestUriValue.indexOf("?") + 1);
        }

        if ("application/x-www-form-urlencoded".equalsIgnoreCase(headers.getValue(CONTENT_TYPE))) {
            return body;
        }
        return "";
    }

    public boolean isMatching(final String requestPath, final RequestMethod requestMethod) {
        return requestLine.isMatching(requestPath, requestMethod);
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

    public HttpCookie getHttpCookie() {
        return httpCookie;
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
                ", httpCookie=" + httpCookie +
                ", body='" + body + '\'' +
                '}';
    }
}
