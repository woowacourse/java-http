package org.apache.coyote.handler.post;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import java.util.UUID;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;

public class UserRegisterRequestPostHandler implements RequestHandler {

    private static final String REGISTER_SUCCESS_REDIRECT_URI = "/index.html";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final RequestBody requestBody = httpRequest.requestBody();
        final String account = requestBody.getBodyValue("account");
        final String password = requestBody.getBodyValue("password");
        final String email = requestBody.getBodyValue("email");

        InMemoryUserRepository.save(new User(account, password, email));

        final Session newSession = new Session(UUID.randomUUID().toString());
        newSession.setAttribute("account", account);
        SessionManager.add(newSession);

        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setCookies(Cookies.ofJSessionId(newSession.id()))
                .sendRedirect(REGISTER_SUCCESS_REDIRECT_URI)
                .build();
    }
}
