package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.StaticResourceService;

public class StaticResourceController implements Controller {

    private final StaticResourceService staticResourceService;

    public StaticResourceController(StaticResourceService staticResourceService) {
        this.staticResourceService = staticResourceService;
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        try {
            StaticResource staticResource = staticResourceService.findByPath(httpRequest.getUri());

            return HttpResponse.withBody(HttpStatus.OK, staticResource);
        } catch (NullPointerException exception) {
            StaticResource staticResource = staticResourceService.findByPath("/404.html");

            return HttpResponse.withBody(HttpStatus.NOT_FOUND, staticResource);
        }
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        return doGet(httpRequest);
    }

    @Override
    public boolean matchUri(String uri) {
        return false;
    }
}
