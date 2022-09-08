package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;
import static org.apache.coyote.http11.HttpCookie.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {

    static final String HTTP_METHOD = "HTTP METHOD";
    private static final String REQUEST_URI = "REQUEST URI";
    private static final String HTTP_VERSION = "HTTP VERSION";
    private static final String HEADER_DELIMITER = ":";
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String MESSAGE_DIVIDER = "\r\n\r\n";
    private static final String LINE_BREAK = "\r\n";
    private static final String EMPTY_MESSAGE_BODY = "";

    private final Map<String, String> headers = new HashMap<>();
    private final QueryParams queryParams;

    public HttpRequest(final InputStream inputStream) throws IOException, URISyntaxException {
        final String message = readMessage(inputStream);
        parseHeaders(message);
        this.queryParams = new QueryParams(getUri(), parseMessageBody(message));
    }

    private String readMessage(final InputStream inputStream) {
        final HttpReader reader = new HttpReader(inputStream);
        return reader.readHttpRequest();
    }

    private void parseHeaders(final String message) {
        final String headers = message.split(MESSAGE_DIVIDER)[0];
        for (String header : headers.split(LINE_BREAK)) {
            putHeader(header);
        }
    }

    private void putHeader(final String requestLine) {
        if (!headers.isEmpty()) {
            final List<String> headerAndValue = parseRequestLine(requestLine, HEADER_DELIMITER);
            headers.put(headerAndValue.get(0), headerAndValue.get(1));
            return;
        }
        final List<String> startLine = parseRequestLine(requestLine, REQUEST_LINE_DELIMITER);
        headers.put(HTTP_METHOD, startLine.get(0));
        headers.put(REQUEST_URI, startLine.get(1));
        headers.put(HTTP_VERSION, startLine.get(2));
    }

    private List<String> parseRequestLine(final String requestLine, final String delimiter) {
        return Arrays.stream(requestLine.split(delimiter))
            .map(String::trim)
            .collect(toList());
    }

    private String parseMessageBody(final String message) {
        final String[] split = message.split(MESSAGE_DIVIDER);
        if (split.length < 2) {
            return EMPTY_MESSAGE_BODY;
        }
        return split[1];
    }

    public boolean isGetRequest() {
        return getHttpMethod().equals("GET");
    }

    public boolean isRootPath() {
        return getPath().equals("/");
    }

    public String getPath() {
        return getUri().getPath();
    }

    private URI getUri() {
        try {
            return new URI("http://" + getHeaderValue("Host") + getHeaderValue(REQUEST_URI));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI requested");
        }
    }

    public boolean containsHeader(final String headerName) {
        return headers.containsKey(headerName);
    }

    public boolean hasSession() {
        return containsHeader(COOKIE) &&
            getHeaderValue(COOKIE).contains(JSESSIONID);
    }

    public Session getSession() {
        validateJSESSIONIDExist();
        final HttpCookie cookie = new HttpCookie(getHeaderValue(COOKIE));
        return new Session(cookie.getCookieValue(JSESSIONID));
    }

    private void validateJSESSIONIDExist() {
        if (!hasSession()) {
            throw new IllegalArgumentException("JSESSIONID Not Found");
        }
    }

    public String getHeaderValue(final String headerName) {
        return headers.get(headerName);
    }

    public String getQueryValue(final String queryKey) {
        return queryParams.getQueryValue(queryKey);
    }

    public String getHttpMethod() {
        return getHeaderValue(HTTP_METHOD);
    }

    public String getHttpVersion() {
        return getHeaderValue(HTTP_VERSION);
    }
}
