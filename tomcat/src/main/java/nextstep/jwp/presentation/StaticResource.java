package nextstep.jwp.presentation;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import nextstep.jwp.exception.ResourceNotFoundException;
import org.apache.coyote.HttpRequest;

public class StaticResource {

    private static final String STATIC_PREFIX = "static";
    private static final String HTML_EXTENSION = ".html";

    public static final String INDEX_PAGE = "static/index.html";
    public static final String UNAUTHORIZED_PAGE = "static/401.html";
    public static final String NOT_FOUND_PAGE = "static/404.html";
    public static final String INTERNAL_SERVER_ERROR_PAGE = "Static/500.html";

    private StaticResource() {
    }

    public static Path ofRequest(final HttpRequest request) throws URISyntaxException {
        final String resourceName = readResourceName(request);
        return findPath(resourceName);
    }

    private static String readResourceName(final HttpRequest request) {
        String resourceName = request.getPath();
        if (!request.isResource()) {
            resourceName += HTML_EXTENSION;
        }

        return STATIC_PREFIX + resourceName;
    }

    public static Path notFound() throws URISyntaxException {
        return findPath(NOT_FOUND_PAGE);
    }

    private static Path findPath(final String resourceName) throws URISyntaxException {
        final URL resource = findResource(resourceName);
        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        return Path.of(resource.toURI());
    }

    private static URL findResource(final String resourceName) {
        return StaticResource.class
                .getClassLoader()
                .getResource(resourceName);
    }
}
