package nextstep.jwp.controller;

import static nextstep.jwp.http.request.Method.GET;

import java.io.IOException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.StaticResourceService;

public abstract class RestController implements Controller {

    private final StaticResourceService staticResourceService;

    public RestController(StaticResourceService staticResourceService) {
        this.staticResourceService = staticResourceService;
    }

    protected abstract HttpResponse doPost(HttpRequest httpRequest);

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        try {
            StaticResource staticResource = staticResourceService.findByPathWithExtension(
                httpRequest.getUri(), ".html");

            return HttpResponse.withBody(HttpStatus.OK, staticResource);
        } catch (NullPointerException e) {
            StaticResource staticResource = staticResourceService.findByPath("/404.html");

            return HttpResponse.withBody(HttpStatus.NOT_FOUND, staticResource);
        }
    }

    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        if (httpRequest.hasMethod(GET)) {
            return doGet(httpRequest);
        }

        return doPost(httpRequest);
    }
}
