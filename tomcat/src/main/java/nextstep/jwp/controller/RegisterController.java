package nextstep.jwp.controller;

import handler.Controller;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RegisterController implements Controller {

    @Override
    public String run(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpMethod method = httpRequest.getMethod();
        if (method.equals(HttpMethod.GET)) {
            httpResponse.setStatusCode(HttpStatusCode.OK);
            return "/register.html";
        }
        if (method.equals(HttpMethod.POST)) {
            final Map<String, String> requestBody = httpRequest.getRequestBody();
            final String account = requestBody.get("account");
            final String password = requestBody.get("password");
            final String email = requestBody.get("email");
            InMemoryUserRepository.save(new User(account, password, email));
            httpResponse.setStatusCode(HttpStatusCode.FOUND);
            httpResponse.setHeader("Location", "/index.html");
            return "/index.html";
        }
        return "/index.html";
    }
}
