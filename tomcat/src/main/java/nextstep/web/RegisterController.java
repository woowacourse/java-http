package nextstep.web;

import nextstep.jwp.application.RegisterService;
import org.apache.coyote.http11.mvc.AbstractController;
import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = new RegisterService();

    @Override
    public ResponseEntity doGetRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.forwardTo("/register.html");
    }

    @Override
    public ResponseEntity doPostRequest(final HttpRequest request, final HttpResponse response) {
        final String account = request.getPayloadValue("account");
        final String password = request.getPayloadValue("password");
        final String email = request.getPayloadValue("email");

        registerService.register(account, password, email);

        return ResponseEntity.redirectTo("/index.html");
    }
}
