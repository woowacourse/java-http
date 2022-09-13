package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends Controller {
    @Override
    protected void processPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> body = httpRequest.getBody();
        final String email = body.get("email");
        final String account = body.get("account");
        final String password = body.get("password");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        httpResponse.sendRedirect("/index.html");
    }

    @Override
    protected void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setView("register");
    }
}
