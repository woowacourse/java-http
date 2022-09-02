package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.exception.DuplicationException;
import nextstep.jwp.service.RegisterService;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController implements Controller {

    private final RegisterService registerService;

    public RegisterController() {
        registerService = new RegisterService();
    }

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        if (httpRequest.getMethod().equals(HttpMethod.GET)) {
            return show();
        }
        if (httpRequest.getMethod().equals(HttpMethod.POST)) {
            return register(httpRequest.parseBodyQueryString());
        }
        return HttpResponse.found("/404.html");
    }

    public HttpResponse show() {
        return HttpResponse.ok(StaticResource.path("/register.html"));
    }

    public HttpResponse register(final Map<String, String> parameters) {
        try {
            registerService.register(parameters);
            return HttpResponse.found("/index.html");
        } catch (DuplicationException e) {
            return HttpResponse.found("/register.html");
        }
    }
}
