package nextstep.jwp.controller.register;

import nextstep.jwp.controller.base.AbstractController;
import nextstep.jwp.exception.DuplicatedAccountException;
import nextstep.jwp.exception.InvalidEmailFormException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Status;

import java.util.Map;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws Exception {
        return super.handle(httpRequest);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.okWithResource("/register.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        Map<String, String> params = httpRequest.getBody().getParams();

        try {
            userService.register(params.get("account"), params.get("password"), params.get("email"));
            return HttpResponse.withResource(Status.FOUND, "/index.html");
        } catch (DuplicatedAccountException | InvalidEmailFormException e) {
            return HttpResponse.withResource(Status.BAD_REQUEST, "/400.html");
        }
    }
}
