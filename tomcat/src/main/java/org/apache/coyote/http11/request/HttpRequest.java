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

    private static final String HEADER_DELIMITER = ":";
    private static final String MESSAGE_DIVIDER = "\r\n\r\n";
    private static final String LINE_BREAK = "\r\n";
    private static final String EMPTY_MESSAGE_BODY = "";

    private final Map<String, String> headers = new HashMap<>();
    private final RequestLine requestLine;
    private final QueryParams queryParams;

    public HttpRequest(final InputStream inputStream) throws IOException, URISyntaxException {
        final String message = readMessage(inputStream);
        this.requestLine = new RequestLine(message);
        parseHeaders(message);
        this.queryParams = new QueryParams(getUri(), parseMessageBody(message));
    }

    private String readMessage(final InputStream inputStream) {
        final HttpReader reader = new HttpReader(inputStream);
        return reader.readHttpRequest();
    }

    private void parseHeaders(final String message) {
        int index = message.indexOf(LINE_BREAK);
        final String messageRemovedRequestLine = message.substring(index + 1);
        final String headers = messageRemovedRequestLine.split(MESSAGE_DIVIDER)[0];

        for (String header : headers.split(LINE_BREAK)) {
            putHeader(header);
        }
    }

    private void putHeader(final String headerLine) {
        final List<String> headerAndValue = parseHeaderLine(headerLine);
        headers.put(headerAndValue.get(0), headerAndValue.get(1));
    }

    private List<String> parseHeaderLine(final String headerLine) {
        return Arrays.stream(headerLine.split(HEADER_DELIMITER))
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
        return requestLine.isGet();
    }

    public boolean isRootPath() {
        return getPath().equals("/");
    }

    public String getPath() {
        return getUri().getPath();
    }

    private URI getUri() {
        try {
            return new URI("http://" + getHeaderValue("Host") + requestLine.getUri());
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

    public String getHttpVersion() {
        return requestLine.getVersion();
    }
}
