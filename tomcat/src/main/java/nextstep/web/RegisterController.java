package nextstep.web;

import nextstep.jwp.application.RegisterService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.web.AbstractController;
import org.apache.coyote.http11.web.View;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = new RegisterService();

    @Override
    public View handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return forwardTo("/register.html");
    }

    @Override
    public View handlePostRequest(final HttpRequest request, final HttpResponse response) {
        final String account = request.getPayloadValue("account");
        final String password = request.getPayloadValue("password");
        final String email = request.getPayloadValue("email");

        registerService.register(account, password, email);

        return redirect("/index.html", response);
    }
}
