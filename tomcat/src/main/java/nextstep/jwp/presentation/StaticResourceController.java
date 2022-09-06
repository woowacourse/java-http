package nextstep.jwp.presentation;

import java.net.URL;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;

public class StaticResourceController extends AbstractController {

    private static final StaticResourceController instance = new StaticResourceController();

    private static final String STATIC_PREFIX = "static";
    private static final String NOT_FOUND_PAGE = "static/404.html";

    private static final String HTML_EXTENSION = ".html";

    private StaticResourceController() {
    }

    public static StaticResourceController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final URL resource = getResource(request, response);

        response.setBody(resource);
    }

    private URL getResource(final HttpRequest request, final HttpResponse response) {
        final String resourceName = getResourceName(request);
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (resource == null) {
            resource = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
            response.setStatus(HttpStatus.NOT_FOUND);
        }
        return resource;
    }

    private static String getResourceName(final HttpRequest request) {
        String requestUri = request.getPath();
        if (!request.isResource()) {
            requestUri += HTML_EXTENSION;
        }

        return STATIC_PREFIX + requestUri;
    }
}
