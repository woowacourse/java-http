package nextstep.jwp.controller;

import nextstep.jwp.exception.DuplicationException;
import nextstep.jwp.service.RegisterService;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController() {
        registerService = new RegisterService();
    }

    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.ok(StaticResource.path("/register.html"));
    }

    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var parameters = httpRequest.parseBodyQueryString();

        try {
            registerService.register(parameters);
            httpResponse.found("/index.html");
        } catch (DuplicationException e) {
            httpResponse.found("/register.html");
        }
    }
}
