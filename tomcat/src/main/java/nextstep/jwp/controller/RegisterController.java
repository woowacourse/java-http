package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ViewResolver;

public class RegisterController extends AbstractController {

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        final String responseBody = ViewResolver.read("/register.html");
        response.setResponseBody(responseBody);
        response.setStatusCode(HttpStatusCode.OK);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, Object> requestBody = request.getRequestBody();
        final String account = (String) requestBody.get("account");
        final String password = (String) requestBody.get("password");
        final String email = (String) requestBody.get("email");
        InMemoryUserRepository.save(new User(account, password, email));
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation("/index.html");
    }
}
