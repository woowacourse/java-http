package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestUri {

    private static final String DEFAULT_RESPONSE = "Hello world!";
    private final String path;
    private final Map<String, String> queryStrings;

    private RequestUri(String path, Map<String, String> queryStrings) {
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public static RequestUri from(String requestUri) {
        int index = requestUri.indexOf("?");

        if (index == -1) {
            return new RequestUri(requestUri, new HashMap<>());
        }

        String path = requestUri.substring(1, index);
        String queryString = requestUri.substring(index + 1);

        Map<String, String> queryStrings = Stream.of(queryString.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(parts -> parts[0], strings -> strings[1]));

        return new RequestUri(path, queryStrings);
    }

    public HttpResponse extractHttpResponse() throws URISyntaxException {
        String responseBody = extractResponseBody();

        if (responseBody.equals(DEFAULT_RESPONSE)) {
            return new HttpResponse(null, responseBody);
        }

        return new HttpResponse(getFileExtension(), responseBody);
    }

    private String getFileExtension() throws URISyntaxException {
        File file = findPath().toFile();
        String fileName = file.getName();

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String extractResponseBody() {
        try {
            return Files.readString(findPath());
        } catch (IOException | URISyntaxException e) {
            return DEFAULT_RESPONSE;
        }
    }

    private Path findPath() throws URISyntaxException {
        String uri = path.substring(1);
        URL resource = getClass().getClassLoader()
                .getResource(String.format("static/%s", uri));

        return Paths.get(Objects.requireNonNull(resource).toURI());
    }

}
