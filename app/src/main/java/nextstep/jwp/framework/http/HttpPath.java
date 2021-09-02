package nextstep.jwp.framework.http;

import java.net.URL;
import java.util.Objects;

public class HttpPath {

    private static final String QUERY_DELIMITER = "\\?";
    private static final String PATH_DELIMITER = "/";
    private static final String RESOURCE_PATH = "static/";
    private static final String PARSING = ".html";
    private static final int FILE_NAME_INDEX = 0;
    private static final int PATH_INDEX = 0;
    private static final int FILE_ROOT_INDEX = 1;
    private static final int ROOT_FILE_PATH_COUNT = 1;
    private static final String DEFAULT_PAGE = "index.html";
    private static final String UNAUTHORIZED_PAGE = "401.html";
    private static final String NOT_FOUND_PAGE = "404.html";

    private String path;

    public HttpPath(final String path) {
        this.path = createPath(path);
    }

    public static URL index() {
        return HttpPath.class.getClassLoader().getResource(RESOURCE_PATH + DEFAULT_PAGE);
    }

    public static URL unAuthorized() {
        return HttpPath.class.getClassLoader().getResource(RESOURCE_PATH + UNAUTHORIZED_PAGE);
    }

    public static URL notFound() {
        return HttpPath.class.getClassLoader().getResource(RESOURCE_PATH + NOT_FOUND_PAGE);
    }

    public URL findResourceURL() {
        return getClass().getClassLoader().getResource(RESOURCE_PATH + path);
    }

    private String createPath(final String path) {
        String resourcePath = path.substring(FILE_ROOT_INDEX).split(QUERY_DELIMITER)[FILE_NAME_INDEX];

        if (resourcePath.split(PATH_DELIMITER).length == ROOT_FILE_PATH_COUNT) {
            return resourcePath.split(PARSING)[PATH_INDEX] + PARSING;
        }

        return resourcePath;
    }

    public boolean isNotExistFile() {
        return Objects.isNull(getClass().getClassLoader().getResource(RESOURCE_PATH + path));
    }

    public void changeIndexPage() {
        path = DEFAULT_PAGE;
    }

    public String getPath() {
        return path;
    }
}
