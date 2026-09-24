package org.apache.catalina.controller;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class StaticResourceController extends AbstractController {
    private static final String STATIC_RESOURCE_PREFIX = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<Path> resource = findStaticResource(request.getPath());
        if (resource.isEmpty()) {
            response.setError(HttpStatus.NOT_FOUND);
            return;
        }

        Path resourcePath = resource.get();
        String fileExtension = getFileExtensionOf(resourcePath);
        MimeType mimeType = MimeType.determineFromFileName(fileExtension);
        response.setBody(mimeType, Files.readAllBytes(resourcePath));
    }

    private static String getFileExtensionOf(Path resourcePath) {
        if (resourcePath == null || !Files.isRegularFile(resourcePath)) {
            return "";
        }
        String fileName = resourcePath.getFileName().toString();
        int lastIndexOfDot = fileName.lastIndexOf('.');

        if (lastIndexOfDot == -1) {
            return "";
        }

        return fileName.substring(lastIndexOfDot + 1);
    }

    private Optional<Path> findStaticResource(String path) throws URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(STATIC_RESOURCE_PREFIX + path);
        if (resourceUrl == null) {
            return Optional.empty();
        }
        return Optional.of(Path.of(resourceUrl.toURI()))
                .filter(Files::isRegularFile);
    }
}
