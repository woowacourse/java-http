package nextstep.jwp.ui;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.model.HttpCookie;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.HttpStatusCode;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final LoginController controller = new LoginController();

    private LoginController() {
    }

    public static LoginController getController() {
        return controller;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        if (request.isLoginRequestWithAuthorization()) {
            return loginWithAuthorization(request);
        }
        return createGetResponseFrom(request);
    }

    private HttpResponse loginWithAuthorization(HttpRequest request) throws IOException {
        Session session = request.getSession();

        if (SessionManager.getSessionManager().containsSession(session)) {
            Map<String, String> cookies = Map.of("JSESSIONID", session.getId());
            HttpCookie httpCookie = HttpCookie.of(cookies);

            return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND)
                .setCookie(httpCookie.toMessage());
        }
        return createGetResponseFrom(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getBodyParam("account"));

        if (user.isEmpty()) {
            return redirectTo("/401", HttpStatusCode.HTTP_STATUS_UNAUTHORIZED);
        }

        User existingUser = user.get();
        if (existingUser.checkPassword(request.getBodyParam("password"))) {
            return redirectTo("/401", HttpStatusCode.HTTP_STATUS_UNAUTHORIZED);
        }

        Session session = request.getSession();
        session.setAttribute("user", existingUser);
        SessionManager.getSessionManager().add(session);

        Map<String, String> cookies = Map.of("JSESSIONID", session.getId());
        HttpCookie httpCookie = HttpCookie.of(cookies);

        return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND)
            .setCookie(httpCookie.toMessage());
    }
}
