package org.apache.coyote.http11.httprequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequestConvertor {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String HEADER_DELIMITER = ":";
    private static final String BODY_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String BODY_DELIMITER = "&";
    private static final String TUPLE_DELIMITER = "=";
    private static final int TUPLE_MIN_LENGTH = 2;
    private static final int TUPLE_KEY_INDEX = 0;
    private static final int TUPLE_VALUE_INDEX = 1;
    private static final int HEADER_KEY_INDEX = 0;
    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final String REQUEST_LINE_DELIMITER = " ";

    public static HttpRequest convertHttpRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        validateRequestLine(requestLine);
        HttpRequestLine httpRequestLine = new HttpRequestLine(requestLine);

        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(getHeaders(bufferedReader));
        Session session = getOrCreateSession(httpRequestHeader);

        if (isExistRequestBody(httpRequestHeader)) {
            HttpRequestBody httpRequestBody = getHttpRequestBody(bufferedReader, httpRequestHeader);
            return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody, session);
        }

        return new HttpRequest(httpRequestLine, httpRequestHeader, session);
    }

    private static void validateRequestLine(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("요청이 비어 있습니다");
        }
        if (requestLine.split(REQUEST_LINE_DELIMITER).length < 3) {
            throw new IllegalArgumentException("RequestLine이 잘못된 요청입니다");
        }
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] requestLine = line.split(HEADER_DELIMITER);
            headers.put(requestLine[HEADER_KEY_INDEX], parseHeaderValue(requestLine));
        }

        return headers;
    }

    private static String parseHeaderValue(String[] requestLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < requestLine.length; i++) {
            sb.append(requestLine[i].strip());
            if (i != requestLine.length - 1) {
                sb.append(HEADER_DELIMITER);
            }
        }

        return sb.toString();
    }

    private static Session getOrCreateSession(HttpRequestHeader httpRequestHeader) {
        if (!httpRequestHeader.containsHeader(HttpHeaderName.COOKIE)) {
            return createSession();
        }

        HttpCookie httpCookie = HttpCookieConvertor.convertHttpCookie(
                httpRequestHeader.getHeaderValue(HttpHeaderName.COOKIE));
        if (!httpCookie.containsCookie(JSESSIONID)) {
            return createSession();
        }

        return getOrCreateSession(httpCookie);
    }

    private static Session getOrCreateSession(HttpCookie httpCookie) {
        String jsessionid = httpCookie.getCookieValue(JSESSIONID);
        if (!SESSION_MANAGER.containsSession(jsessionid)) {
            return createSession();
        }
        return SESSION_MANAGER.findSession(jsessionid);
    }

    private static Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        SESSION_MANAGER.add(session);
        return session;
    }

    private static HttpRequestBody getHttpRequestBody(
            BufferedReader bufferedReader,
            HttpRequestHeader httpRequestHeader
    ) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.getHeaderValue(HttpHeaderName.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        System.out.println(requestBody);
        Map<String, String> body = extractBody(requestBody);
        return new HttpRequestBody(body);
    }

    private static Map<String, String> extractBody(String requestBody) {
        String[] tokens = requestBody.split(BODY_DELIMITER);
        return Arrays.stream(tokens)
                .filter(token -> token.split(TUPLE_DELIMITER).length >= TUPLE_MIN_LENGTH)
                .map(token -> token.split(TUPLE_DELIMITER))
                .collect(Collectors.toMap(
                        token -> token[TUPLE_KEY_INDEX],
                        token -> token[TUPLE_VALUE_INDEX]
                ));
    }

    private static boolean isExistRequestBody(HttpRequestHeader httpRequestHeader) {
        return httpRequestHeader.containsHeader(HttpHeaderName.CONTENT_LENGTH)
                && httpRequestHeader.getHeaderValue(HttpHeaderName.CONTENT_TYPE).equals(BODY_FORM_CONTENT_TYPE);
    }
}
