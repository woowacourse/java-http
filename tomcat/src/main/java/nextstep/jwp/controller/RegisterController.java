package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpUri;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

public class RegisterController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> registerData = request.getBody();
        InMemoryUserRepository.save(new User(registerData.get("account"), registerData.get("password"), registerData.get("email")));
        response.ok(HttpUri.INDEX_HTML.getUri());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.ok(HttpUri.REGISTER.getUri());
    }
}
