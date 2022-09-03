package nextstep.jwp.controller;

import java.net.URL;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;

public class StaticResourceController extends AbstractController {

    private static final StaticResourceController instance = new StaticResourceController();

    private static final String STATIC_PREFIX = "static";
    private static final String NOT_FOUND_PAGE = "static/404.html";

    private StaticResourceController() {
    }

    public static StaticResourceController getInstance() {
        return instance;
    }

    @Override
    void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final URL resource = getResource(request, response);

        response.setBody(resource);
    }

    private URL getResource(final HttpRequest request, final HttpResponse response) {
        final String requestUri = request.getPath();
        final String resourceName = STATIC_PREFIX + requestUri;
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (resource == null) {
            resource = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
            response.setStatus(HttpStatus.NOT_FOUND);
        }
        return resource;
    }
}
