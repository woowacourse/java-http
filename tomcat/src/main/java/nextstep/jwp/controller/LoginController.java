package nextstep.jwp.controller;

import static nextstep.jwp.utils.FileUtils.getResource;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.handler.LoginHandler;
import org.apache.http.ContentType;
import org.apache.http.Cookies;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.QueryParams;
import org.apache.http.StatusCode;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.FileUtils;
import org.apache.controller.AbstractController;
import org.apache.session.Session;
import org.apache.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        return handleLoginPage(request);
    }

    private HttpResponse handleLoginPage(HttpRequest httpRequest) {
        Cookies cookies = httpRequest.getCookies();
        if (SessionManager.isValidSession(cookies)) {
            return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource("/index.html")));
        }
        return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/login.html")));
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        return login(request);
    }

    private HttpResponse login(HttpRequest httpRequest) {
        QueryParams queryParams = httpRequest.getFormData();
        if (LoginHandler.canLogin(queryParams)) {
            User user = InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT_KEY))
                .orElseThrow(UserNotFoundException::new);
            return HttpResponse.of(StatusCode.FOUND, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource("/index.html")), setCookie(user));
        }
        return HttpResponse.of(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/401.html")));
    }

    private Cookies setCookie(User user) {
        Session session = SessionManager.generateNewSession();
        session.createAttribute("user", user);
        SessionManager.add(session);
        return Cookies.fromJSessionId(session.getId());
    }
}
