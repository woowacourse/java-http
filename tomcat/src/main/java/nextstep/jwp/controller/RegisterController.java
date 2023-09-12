package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.HttpController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.Set;

import static org.apache.coyote.http11.common.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;

public class RegisterController extends HttpController {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final Set<String> requestType = Set.of("/register");
        return requestType.contains(httpRequest.getTarget());
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        handleResource("/register.html", httpRequest, httpResponse);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final User newUser = new User(
                httpRequest.getBody().get("account"),
                httpRequest.getBody().get("password"),
                httpRequest.getBody().get("email")
        );
        InMemoryUserRepository.save(newUser);
        httpResponse.addHeader(LOCATION, "/index.html");
        httpResponse.setStatusCode(FOUND);
    }
}
