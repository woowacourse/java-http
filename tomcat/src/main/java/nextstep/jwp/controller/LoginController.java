package nextstep.jwp.controller;

import static org.apache.coyote.http11.support.HeaderField.SET_COOKIE;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.http11.support.HttpCookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;
import org.apache.coyote.http11.response.ResponseHeaders;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        if (isExistSession(request)) {
            return HttpResponseBuilder.found("/index");
        }
        return HttpResponseBuilder.ok(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        final String account = request.getBodyValue("account");
        final String password = request.getBodyValue("password");

        final UUID uuid = UUID.randomUUID();
        final User user = loginService.login(account, password);
        addSession(uuid, user);

        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(SET_COOKIE, "JSESSIONID=" + uuid);
        return HttpResponseBuilder.found("/index", headers);

    }

    private void addSession(UUID uuid, User user) {
        final Session session = new Session(uuid.toString());
        session.setAttribute("user", user);
        SessionManager.add(session);
    }

    private boolean isExistSession(HttpRequest request) {
        final HttpCookie cookie = request.getCookie();
        final String jSessionId = cookie.getJSESSIONID();
        final Optional<Session> session = SessionManager.findSession(jSessionId);
        return session.isPresent();
    }
}
