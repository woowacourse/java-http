package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class RequestHandler {

    private static final String DEFAULT_RESPONSE = "Hello world!";

    private final RequestLine requestLine;

    public RequestHandler(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public HttpResponse extractHttpResponse() {
        String path = requestLine.getPath();
        String responseBody = extractResponseBody();

        if (path.equals("/")) {
            return HttpResponse.of(FileExtension.HTML, responseBody);
        }
        return HttpResponse.of(getFileExtension(), responseBody);
    }

    private String extractResponseBody() {
        try {
            return Files.readString(findPath());
        } catch (IOException | URISyntaxException | NullPointerException e) {
            return DEFAULT_RESPONSE;
        }
    }

    private Path findPath() throws URISyntaxException {
        URL resource = getClass().getClassLoader()
                .getResource(String.format("static/%s", findResourcePath()));

        return Paths.get(Objects.requireNonNull(resource).toURI());
    }

    public String findResourcePath() {
        String path = requestLine.getPath();
        String fileName = path.substring(1);

        if (fileName.contains(".")) {
            return fileName;
        }
        return fileName + ".html";
    }

    private FileExtension getFileExtension() {
        try {
            File file = findPath().toFile();
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

            return FileExtension.from(fileExtension);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("해당 파일을 찾을 수 없습니다.");
        }
    }
}
