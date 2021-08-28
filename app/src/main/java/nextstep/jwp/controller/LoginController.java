package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.StaticResourceService;

public class LoginController implements Controller {

    private final StaticResourceService staticResourceService;

    public LoginController(StaticResourceService staticResourceService) {
        this.staticResourceService = staticResourceService;
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        StaticResource staticResource = staticResourceService.findByPathWithExtension(
            httpRequest.getUri(), ".html");

        return HttpResponse.of(HttpStatus.OK, staticResource);
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        return doGet(httpRequest);
    }

    @Override
    public boolean matchUri(String uri) {
        return uri.startsWith("/login");
    }
}
