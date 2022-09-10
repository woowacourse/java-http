package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidUserException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.QueryParams;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doGet(request, response);
        checkAlreadyLoginUser(request, response);
    }

    private void checkAlreadyLoginUser(final HttpRequest request, final HttpResponse response) {
        HttpCookie cookie = new HttpCookie(request.getCookie());
        Session session = SESSION_MANAGER.findSession(cookie.getSessionId());
        if (session != null) {
            response.sendRedirect(INDEX_PAGE_URL);
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doPost(request, response);
        Login(request, response);
    }

    private void Login(final HttpRequest request, final HttpResponse response) {
        QueryParams queryParams = new QueryParams(request.getBody());
        Map<String, String> queries = queryParams.getValues();
        String account = queries.get("account");
        String password = queries.get("password");

        User user = findUser(account);
        if (!user.checkPassword(password)) {
            throw new InvalidUserException("비밀번호가 일치하지 않습니다.");
        }

        Session session = createSession(user);
        response.setCookie(HttpCookie.fromJSessionId(session.getId()));
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
    }

    private Session createSession(final User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
        return session;
    }
}
