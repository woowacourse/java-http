package org.apache.coyote.ioprocessor.parser;

import com.techcourse.infrastructure.PresentationResolver;
import http.HttpRequestBody;
import http.HttpRequestHeaders;
import http.HttpRequestLine;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class RequestParser {

    private static final String STATIC_RESOURCE_ROOT = "/static";
    private static final String REQUEST_PART_DELIMITER = "\r\n\r\n";
    private static final String REQUEST_HEADER_DELIMITER = "\r\n";
    private final HttpRequestLine requestLine;
    private final HttpRequestHeaders requestHeaders;
    private final HttpRequestBody requestBody;
    private final PresentationResolver resolver;

    public RequestParser(String httpRequest) {
        String[] requestParts = httpRequest.split(REQUEST_PART_DELIMITER, -1);
        validateRequestPartsLength(requestParts);
        String[] requestHeaders = requestParts[0].split(REQUEST_HEADER_DELIMITER);
        this.requestLine = new HttpRequestLine(requestHeaders[0]);
        this.requestHeaders = new HttpRequestHeaders(buildRequestHeader(requestHeaders));
        this.requestBody = new HttpRequestBody(requestParts[1]);
        this.resolver = new PresentationResolver();
    }

    private List<String> buildRequestHeader(String[] requestHeaders) {
        return Arrays.stream(requestHeaders)
                .skip(1)
                .toList();
    }

    private void validateRequestPartsLength(String[] requestParts) {
        if (requestParts.length < 2) {
            throw new RuntimeException("oh invalid protocol");
        }
    }

    public String readResource() throws IOException, URISyntaxException {
        URI requestUri = requestLine.getUri();
        String webPath = requestUri.getPath();
        String queryParam = requestUri.getQuery();
        resolver.view(requestLine.getHttpMethod(), webPath, queryParam);
        URL staticResourceURL = processResourceUri(requestUri);
        Path path = Paths.get(staticResourceURL.toURI());
        return Files.readString(path);
    }

    private URL processResourceUri(URI requestUri) {
        String path = requestUri.getPath();
        URL resource = getClass().getResource(STATIC_RESOURCE_ROOT + path);
        if (resource == null) {
            return getClass().getResource(STATIC_RESOURCE_ROOT + path + ".html");
        }
        return resource;
    }

    public boolean isRootUri() {
        return requestLine.isRootUri();
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpRequestHeaders getRequestHeaders() {
        return requestHeaders;
    }
}
