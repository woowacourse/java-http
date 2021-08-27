package nextstep.jwp.controller;

import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.StaticResourceService;

public class StaticResourceController {

    private final StaticResourceService staticResourceService;

    public StaticResourceController(StaticResourceService staticResourceService) {
        this.staticResourceService = staticResourceService;
    }

    public HttpResponse doGet(HttpRequest httpRequest) {
        StaticResource staticResource = staticResourceService.findByPath(httpRequest.getUri());

        return HttpResponse.of(HttpStatus.OK, staticResource);
    }
}
