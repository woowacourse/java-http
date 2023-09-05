package nextstep.jwp.controller.register;

import javassist.NotFoundException;
import nextstep.jwp.controller.base.AbstractController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Status;

import java.io.IOException;
import java.net.URISyntaxException;
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
    protected HttpResponse doPost(final HttpRequest httpRequest) throws NotFoundException, IOException, URISyntaxException {
        Map<String, String> params = httpRequest.getBody().getParams();
        userService.register(params.get("account"), params.get("password"), params.get("email"));

        return HttpResponse.withResource(Status.FOUND, "/index.html");
    }
}
