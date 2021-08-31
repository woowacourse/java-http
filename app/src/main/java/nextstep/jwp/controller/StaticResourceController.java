package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.exception.StaticResourceNotFoundException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.StaticResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceController implements Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceController.class);

    private final StaticResourceService staticResourceService;

    public StaticResourceController(StaticResourceService staticResourceService) {
        this.staticResourceService = staticResourceService;
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        try {
            StaticResource staticResource = staticResourceService.findByPath(httpRequest.getUri());

            return HttpResponse.withBody(HttpStatus.OK, staticResource);
        } catch (StaticResourceNotFoundException e) {
            StaticResource staticResource = staticResourceService.findByPath("/404.html");

            LOGGER.warn(e.getMessage());

            return HttpResponse.withBody(HttpStatus.NOT_FOUND, staticResource);
        }
    }

    @Override
    public HttpResponse service(HttpRequest httpRequest) throws IOException {
        return doGet(httpRequest);
    }
}
