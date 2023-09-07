package nextstep.jwp.handler;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.body.FormData;
import org.apache.coyote.http11.handler.FileHandler;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.header.Cookies;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginHandler implements Handler {

    private static final String SESSION_KEY = "JSESSIONID";
    private static final String UNAUTHORIZED_LOCATION = "/401";
    private static final String MAIN_LOCATION = "/index";

    private final FileHandler fileHandler = new FileHandler();

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (httpRequest.getMethod().isPost()) {
            FormData formData = FormData.of(httpRequest.getBody());
            String account = formData.get("account");
            String password = formData.get("password");

            Optional<User> found = InMemoryUserRepository.findByAccount(account);
            if (found.isEmpty()) {
                return HttpResponse.redirectTo(UNAUTHORIZED_LOCATION);
            }
            User user = found.get();
            return signIn(httpRequest, user, password);
        }
        if (httpRequest.getMethod().isGet()) {
            String jsessionId = httpRequest.getCookies().get(SESSION_KEY);
            if (jsessionId == null) {
                return fileHandler.handle(httpRequest);
            }
            Session session = SessionManager.findSession(jsessionId);
            if (session != null) {
                return HttpResponse.redirectTo(MAIN_LOCATION);
            }
        }
        return fileHandler.handle(httpRequest);
    }

    private HttpResponse signIn(final HttpRequest httpRequest, final User user, final String password) {
        if (user.checkPassword(password)) {
            Session session = new Session();
            session.addAttribute("user", user);
            SessionManager.add(session);

            Cookies cookies = httpRequest.getCookies();
            cookies.add(SESSION_KEY, session.getId());

            return redirectToMainWith(cookies);
        }
        return HttpResponse.redirectTo(UNAUTHORIZED_LOCATION);
    }

    private HttpResponse redirectToMainWith(Cookies cookies) {
        HttpResponse httpResponse = HttpResponse.redirectTo(MAIN_LOCATION);
        httpResponse.putHeader(cookies.toHeader("Set-Cookie"));
        return httpResponse;
    }
}
