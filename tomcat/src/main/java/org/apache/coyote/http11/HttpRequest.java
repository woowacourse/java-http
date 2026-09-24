package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;

final class HttpRequest {

    private static final String JSESSIONID = "JSESSIONID";
    private final RequestLine requestLine;
    private final QueryParameters bodyParameters;
    private final HttpHeaders headers;
    private final Manager manager;
    private HttpSession session;

    private HttpRequest(final RequestLine requestLine,
                        final QueryParameters bodyParameters,
                        final HttpHeaders headers,
                        final Manager manager) {
        this.requestLine = requestLine;
        this.bodyParameters = bodyParameters;
        this.headers = headers;
        this.manager = manager;
    }

    static HttpRequest from(final BufferedReader reader, final Manager manager) throws IOException {
        List<String> headerLines = readLines(reader);
        RequestLine requestLine = RequestLine.from(headerLines.getFirst());
        HttpHeaders headers = HttpHeaders.from(headerLines.subList(1, headerLines.size()));
        QueryParameters bodyParams = QueryParameters.from(readBody(reader, headers));
        return new HttpRequest(requestLine, bodyParams, headers, manager);
    }

    @Nonnull
    private static String readBody(BufferedReader reader, HttpHeaders headers) throws IOException {
        String body = "";

        final int contentLength = headers.getFirst("Content-Length")
                .map(HttpRequest::parseInt)
                .orElse(0);

        validateNonNegative(contentLength);

        char[] bodyBuffer = new char[contentLength];
        int restLength = contentLength;
        while (true) {
            int readLength = reader.read(bodyBuffer, contentLength - restLength, restLength);
            if (readLength == -1) {
                throw new InvalidHttpRequestException("Invalid Content-Length: " + contentLength);
            }
            restLength -= readLength;
            if (restLength == 0) {
                break;
            }
        }
        body = new String(bodyBuffer);

        return body;
    }

    private static void validateNonNegative(Integer integer) {
        if (integer == null || integer < 0) {
            throw new InvalidHttpRequestException("Invalid integer value: " + integer);
        }
    }

    private static int parseInt(String contentLengthString) {
        int contentLength;
        try {
            contentLength = Integer.parseInt(contentLengthString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpRequestException("Content-Length의 값이 숫자가 아닙니다.");
        }
        return contentLength;
    }

    @Nonnull
    private static List<String> readLines(BufferedReader reader) throws IOException {
        String line;
        List<String> headerLines = new ArrayList<>();
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        if (headerLines.isEmpty()) {
            throw new InvalidHttpRequestException("HttpHeader가 비어있습니다.");
        }
        return headerLines;
    }


    String getMethod() {
        return requestLine.getMethod();
    }

    String getPath() {
        return requestLine.getPath();
    }

    boolean matches(final String method, final String path) {
        return requestLine.matches(method, path);
    }

    String getHeader(final String name) {
        return headers.getFirst(name).orElse(null);
    }

    String getParameter(final String name) {
        return requestLine.getParameter(name);
    }

    String getBodyParameter(final String name) {
        return bodyParameters.get(name).orElse(null);
    }

    Cookie getCookie() {
        return Cookie.from(headers.getFirst("Cookie").orElse(null));
    }

    HttpSession getSession() {
        return getSession(true);
    }

    HttpSession getSession(final boolean create) {
        if (session != null) {
            return session;
        }

        session = getCookie().get(JSESSIONID)
                .map(this::findSession)
                .orElse(null);

        if (session == null && create) {
            session = new Session(UUID.randomUUID().toString());
            manager.add(session);
        }
        return session;
    }

    private HttpSession findSession(final String id) {
        try {
            return manager.findSession(id);
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}
