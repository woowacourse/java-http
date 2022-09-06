package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;
import static org.apache.coyote.http11.HttpCookie.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.session.Session;

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

    public HttpRequest(InputStream inputStream) throws IOException, URISyntaxException {
        final String message = readMessage(inputStream);
        parseHeaders(message);
        this.queryParams = new QueryParams(getUri(), parseMessageBody(message));
    }

    private String readMessage(InputStream inputStream) {
        final HttpReader reader = new HttpReader(inputStream);
        return reader.readHttpRequest();
    }

    private void parseHeaders(String message) {
        final String headers = message.split(MESSAGE_DIVIDER)[0];
        for (String header : headers.split(LINE_BREAK)) {
            putHeader(header);
        }
    }

    private void putHeader(String requestLine) {
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

    private List<String> parseRequestLine(String requestLine, String delimiter) {
        return Arrays.stream(requestLine.split(delimiter))
            .map(String::trim)
            .collect(toList());
    }

    private String parseMessageBody(String message) {
        final String[] split = message.split(MESSAGE_DIVIDER);
        if (split.length < 2) {
            return EMPTY_MESSAGE_BODY;
        }
        return split[1];
    }

    public String getHeaderValue(String headerName) {
        return headers.get(headerName);
    }

    private URI getUri() throws URISyntaxException {
        return new URI("http://" + getHeaderValue("Host") + getHeaderValue(REQUEST_URI));
    }

    public URL getUrl() {
        try {
            final URI requestUri = getUri();

            if (requestUri.getPath().equals("/")) {
                return requestUri.toURL();
            }

            if (hasQuery()) {
                return addExtensionToPath(new URI(requestUri.getPath()));
            }

            return addExtensionToPath(requestUri);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid Uri");
        }
    }

    private URL addExtensionToPath(URI requestUri) throws MalformedURLException, URISyntaxException {
        if (!requestUri.getPath().contains(".")) {
            requestUri = new URI(requestUri + ".html");
        }

        final URL resource = getClass().getClassLoader().getResource("static" + requestUri.getPath());
        if (resource == null) {
            return requestUri.toURL();
        }
        return resource;
    }

    public boolean hasQuery() {
        return queryParams.hasQuery();
    }

    public boolean containsHeader(String headerName) {
        return headers.containsKey(headerName);
    }

    public String getQueryValue(String queryKey) {
        return queryParams.getQueryValue(queryKey);
    }

    public String getHttpVersion() {
        return getHeaderValue(HTTP_VERSION);
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

    public boolean hasSession() {
        return containsHeader(COOKIE) &&
            getHeaderValue(COOKIE).contains(JSESSIONID);
    }
}
