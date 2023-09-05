package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class ViewResolver {

    private static final String DEFAULT_RESPONSE = "Hello world!";

    private final ResponseEntity responseEntity;

    public ViewResolver(ResponseEntity responseEntity) {
        this.responseEntity = responseEntity;
    }

    public HttpResponse extractHttpResponse() {
        return new HttpResponse(responseEntity.getHttpStatus(), getFileExtension(), extractResponseBody());
    }

    private MimeType getFileExtension() {
        try {
            File file = findPath().toFile();
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

            return MimeType.from(fileExtension);
        } catch (URISyntaxException | NullPointerException e) {
            return MimeType.HTML;
        }
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

    private String findResourcePath() {
        String path = responseEntity.getPath();
        String fileName = path.substring(1);

        if (fileName.contains(".")) {
            return fileName;
        }
        return fileName + ".html";
    }


}
