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
        StaticResource staticResource = staticResourceService.findByPath(httpRequest.getUri());

        return HttpResponse.withBody(HttpStatus.OK, staticResource);
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
