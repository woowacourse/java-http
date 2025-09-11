package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;

public class StaticResourceHandler {

    public static final String RESOURCE_DIRECTORY = "static";
    public static final String EXTENSION_SEPARATOR = ".";

    public static void handleStaticResource(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isRootPath()) {
            response.setResponse(HttpStatus.OK, MimeType.HTML, "Hello world!");
            return;
        }
        MimeType mimeType = request.resolveMimeType();
        String path = ensureExtension(request, mimeType);
        Path filePath = getFilePath(path);
        if (filePath == null) {
            Path notFoundPath = getFilePath("/404.html");
            String responseBody = Files.readString(notFoundPath, StandardCharsets.UTF_8);
            response.setResponse(HttpStatus.NOT_FOUND, null, responseBody);
            return;
        }
        String responseBody = Files.readString(filePath, StandardCharsets.UTF_8);
        response.setResponse(HttpStatus.OK, mimeType, responseBody);
    }

    private static String ensureExtension(HttpRequest request, MimeType mimeType) {
        String path = request.getPath();
        if (request.getExtension().isEmpty()) {
            path += EXTENSION_SEPARATOR + mimeType;
        }
        return path;
    }

    private static Path getFilePath(String path) {
        URL resource = StaticResourceHandler.class.getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        if (resource == null) {
            return null;
        }
        return new File(resource.getFile()).toPath();
    }
}
