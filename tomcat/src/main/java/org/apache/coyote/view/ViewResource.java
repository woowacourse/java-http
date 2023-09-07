package org.apache.coyote.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.response.HttpStatus;

public class ViewResource extends Resource {

    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String NOT_FOUND_RESOURCE_PATH = "/404.html";
    private static final String HTML_TYPE = ".html";

    private ViewResource(String path, HttpStatus httpStatus) {
        super(path, httpStatus);
    }

    public static ViewResource of(String path, HttpStatus httpStatus) {
        try {
            if (getResourceUrl(path) == null && getResourceUrl(path + HTML_TYPE) != null) {
                return new ViewResource(getResource(path + HTML_TYPE), httpStatus);
            }
            if (getResourceUrl(path) == null) {
                return new ViewResource(getResource(NOT_FOUND_RESOURCE_PATH), HttpStatus.NOT_FOUND);
            }
            return new ViewResource(getResource(path), httpStatus);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException("파일 경로가 잘못되었습니다.");
        }
    }

    private static String getResource(String path) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Path.of(getResourceUrl(path).toURI())));
    }

    private static URL getResourceUrl(String path) {
        return ViewResolver.class.getResource(DEFAULT_RESOURCE_PATH + path);
    }
}
