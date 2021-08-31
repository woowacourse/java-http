package nextstep.jwp.web.controller;

import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    public DefaultController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        log.info("GET {}", path);
        if (getResource().equals(path)) {
            final View view = new View("/index.html");
            return HttpResponse.ofView(HttpStatus.OK, view);
        }
        final View view = new View(path);
        return HttpResponse.ofView(HttpStatus.OK, view);
    }
}
