package nextstep.jwp.web.controller;

import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    public DefaultController(String resource) {
        super(resource);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        final String path = request.getPath();
        log.info("GET {}", path);
        if (getResource().equals(path)) {
            redirect(response, HOME_PAGE);
            return;
        }
        okWithPath(response, path);
    }
}
