package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.util.UUID;

public class RegisterController extends AbstractController {

    private static final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatusCode(StatusCode.OK)
                .setContentType(ContentType.HTML)
                .setRedirect("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final User user = makeUser(request);
        InMemoryUserRepository.save(user);
        final Session session = createSession(user);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        response.setStatusCode(StatusCode.CREATED)
                .setContentType(ContentType.HTML)
                .setRedirect("/index.html");
    }

    private User makeUser(final HttpRequest request) {
        final String account = request.findBodyParameter("account");
        final String email = request.findBodyParameter("email");
        final String password = request.findBodyParameter("password");
        return new User(account, password, email);
    }

    private Session createSession(final User user) {
        final Session session = new Session(String.valueOf(UUID.randomUUID()));
        session.addAttribute("user", user);
        sessionManager.add(session);
        return session;
    }
}
