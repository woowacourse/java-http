package org.apache.coyote.view;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import org.apache.coyote.response.HttpStatus;

public class ViewResource {

    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String NOT_FOUND_RESOURCE_PATH = "/404.html";
    private static final String HTML_TYPE = ".html";

    private final Path path;
    private final HttpStatus httpStatus;

    private ViewResource(Path path, HttpStatus httpStatus) {
        this.path = path;
        this.httpStatus = httpStatus;
    }

    public static ViewResource of(String path, HttpStatus httpStatus) {
        try {
            if (getResource(path) == null && getResource(path + HTML_TYPE) != null) {
                return new ViewResource(Path.of(getResource(path + HTML_TYPE).toURI()), httpStatus);
            }
            if (getResource(path) == null) {
                return new ViewResource(Path.of(getResource(NOT_FOUND_RESOURCE_PATH).toURI()), HttpStatus.NOT_FOUND);
            }
            return new ViewResource(Path.of(getResource(path).toURI()), httpStatus);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("파일 경로가 잘못되었습니다.");
        }
    }

    private static URL getResource(String path) {
        return ViewResolver.class.getResource(DEFAULT_RESOURCE_PATH + path);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Path getPath() {
        return path;
    }
}
