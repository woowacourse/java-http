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

public class HttpRequest {

    private static final String REQUEST_URI = "REQUEST URI";
    private static final String HTTP_METHOD = "HTTP METHOD";
    private static final String HTTP_VERSION = "HTTP VERSION";

    private final Map<String, String> headers;
    private final QueryParams queryParams;

    public HttpRequest(InputStream inputStream) throws IOException, URISyntaxException {
        this.headers = parseHeaders(inputStream);
        this.queryParams = new QueryParams(getUri());
    }

    private Map<String, String> parseHeaders(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final Map<String, String> requestHeaders = new HashMap<>();
        String line = bufferedReader.readLine();

        try {
            while (!"".equals(line)) {
                if (Objects.isNull(line)) {
                    return requestHeaders;
                }
                putHeader(requestHeaders, line);
                line = bufferedReader.readLine();
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("invalid HTTP request received", e.getCause());
        }
        return requestHeaders;
    }

    private void putHeader(Map<String, String> requestHeaders, String requestLine) {
        if (!requestHeaders.isEmpty()) {
            List<String> headerAndValue = parseRequestLine(requestLine, ":");
            requestHeaders.put(headerAndValue.get(0), headerAndValue.get(1));
            return;
        }
        List<String> startLine = parseRequestLine(requestLine, " ");
        requestHeaders.put(HTTP_METHOD, startLine.get(0));
        requestHeaders.put(REQUEST_URI, startLine.get(1));
        requestHeaders.put(HTTP_VERSION, startLine.get(2));
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

    public URL getUrl() throws MalformedURLException, URISyntaxException {
        final URI requestUri = getUri();

        if (requestUri.getPath().equals("/")) {
            return requestUri.toURL();
        }
        if (hasQuery()) {
            return addExtensionToPath(new URI(removeQueryStrings(requestUri)));
        }

        return addExtensionToPath(requestUri);
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

    private boolean hasQuery() {
        return queryParams.hasQuery();
    }

    public boolean containsHeader(String headerName) {
        return headers.containsKey(headerName);
    }

    public boolean hasQueryKey(String queryKey) {
        return queryParams.containsKey(queryKey);
    }

    public String getQueryValue(String queryKey) {
        return queryParams.getQueryValue(queryKey);
    }
}
