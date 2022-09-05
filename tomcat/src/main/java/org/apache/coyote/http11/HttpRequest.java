package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.catalina.session.Session;

public class HttpRequest {

    static final String HTTP_METHOD = "HTTP METHOD";
    private static final String REQUEST_URI = "REQUEST URI";
    private static final String HTTP_VERSION = "HTTP VERSION";

    private final Map<String, String> headers = new HashMap<>();
    private final QueryParams queryParams;

    public HttpRequest(InputStream inputStream) throws IOException, URISyntaxException {
        String messageBody = parseHeaders(inputStream);
        this.queryParams = new QueryParams(getUri());
        this.queryParams.addQuery(messageBody);
    }

    private String parseHeaders(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;

        try {
            while (!"".equals(line = bufferedReader.readLine())) {
                if (Objects.isNull(line)) {
                    return "";
                }
                putHeader(line);
            }

            if (!(containsHeader("Content-Type") &&
                getHeaderValue("Content-Type").equals("application/x-www-form-urlencoded"))) {
                return "";
            }

            int contentLength = Integer.parseInt(getHeaderValue("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);

        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("invalid HTTP request received", e.getCause());
        }
    }

    private void putHeader(String requestLine) {
        if (!headers.isEmpty()) {
            List<String> headerAndValue = parseRequestLine(requestLine, ":");
            headers.put(headerAndValue.get(0), headerAndValue.get(1));
            return;
        }
        List<String> startLine = parseRequestLine(requestLine, " ");
        headers.put(HTTP_METHOD, startLine.get(0));
        headers.put(REQUEST_URI, startLine.get(1));
        headers.put(HTTP_VERSION, startLine.get(2));
    }

    private List<String> parseRequestLine(String requestLine, String delimiter) {
        return Arrays.stream(requestLine.split(delimiter))
            .map(String::trim)
            .collect(toList());
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
                return addExtensionToPath(new URI(removeQueryStrings(requestUri)));
            }

            return addExtensionToPath(requestUri);
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid Uri");
        }
    }

    private String removeQueryStrings(URI requestUri) {
        return requestUri.toString()
            .replace("?" + requestUri.getQuery(), "");
    }

    private URL addExtensionToPath(URI requestUri) throws MalformedURLException, URISyntaxException {
        if (!requestUri.getPath().contains(".")) {
            requestUri = new URI(requestUri + ".html");
        }

        URL resource = getClass().getClassLoader().getResource("static" + requestUri.getPath());
        if (Objects.isNull(resource)) {
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
        HttpCookie cookie = new HttpCookie(getHeaderValue("Cookie"));
        return new Session(cookie.getCookieValue("JSESSIONID"));
    }

    private void validateJSESSIONIDExist() {
        if (!hasSession()) {
            throw new IllegalArgumentException("JSESSIONID Not Found");
        }
    }

    public boolean hasSession() {
        return containsHeader("Cookie") && getHeaderValue("Cookie").contains("JSESSIONID");
    }
}
