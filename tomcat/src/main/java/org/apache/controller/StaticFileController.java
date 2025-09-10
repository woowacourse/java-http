package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.exception.InvalidRequestException;
import org.apache.exception.RequestProcessingException;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.value.ContentType;
import org.apache.http.value.HttpHeader;
import org.apache.http.value.StatusCode;

public class StaticFileController implements Controller {

    private static final String DEFAULT_FILE_EXTENSION = ".html";

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        if (request.getUri().isEmpty() || request.getUri().equals("/")) {
            return false;
        }
        String uri = addDefaultExtension(request.getUri());
        return isExistResource(uri);
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        try {
            String uri = addDefaultExtension(request.getUri());
            validateInvalidUri(uri);

            URL resource = findResourceUrl(uri);
            Path path = Paths.get(resource.toURI());
            String responseBody = Files.readString(path);

            response.setStatusCode(StatusCode.OK);
            response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), getFileExtension(path).getValue());
            response.setBody(responseBody);

        } catch (IOException | URISyntaxException | IllegalArgumentException exception) {
            throw new RequestProcessingException("리소스를 읽는데 오류가 발생했습니다.");
        }
    }

    private String addDefaultExtension(String uri) {
        List<String> uriPart = List.of(uri.split("/"));
        if (uriPart.getLast().contains(".")) {
            return uri;
        }
        return uri + DEFAULT_FILE_EXTENSION;
    }

    private URL findResourceUrl(String uri) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource("static" + uri);
        if (resourceUrl == null) {
            throw new RequestProcessingException("존재하지 않는 리소스 주소입니다.");
        }
        return resourceUrl;
    }

    private void validateInvalidUri(String uri) {
        String decodedUri = URLDecoder.decode(uri, StandardCharsets.UTF_8);
        List<String> uriPart = List.of(decodedUri.split("/"));
        if (uriPart.contains(".") || uriPart.contains("..")) {
            throw new InvalidRequestException("부적절한 리소스 주소입니다.");
        }
    }

    private boolean isExistResource(String uri) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource("static" + uri);
        return resourceUrl != null;
    }

    private ContentType getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        List<String> split = List.of(fileName.split("\\."));
        return ContentType.parse(split.getLast());
    }
}
