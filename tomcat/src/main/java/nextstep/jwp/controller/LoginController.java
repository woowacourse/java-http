package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.AbstractController;
import org.apache.coyote.http.RequestHeader;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestBody;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final LoginController INSTANCE = new LoginController();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        if (alreadyLogin(httpRequest.getHeader())) {
            return HttpResponse.init(HttpStatusCode.FOUND)
                    .setLocationAsHome();
        }

        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath("/login.html");
    }

    public boolean alreadyLogin(final RequestHeader header) {
        if (!header.hasSessionId()) {
            return false;
        }

        final Session session = SessionManager.findSession(header.getSessionId());
        final User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final HttpRequestBody requestBody = httpRequest.getRequestBody();
        final String account = requestBody.get("account");
        final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(account);

        if (possibleUser.isEmpty()) {
            log.info("존재하지 않는 유저 [요청 account: {}]", account);
            return HttpResponse.init(HttpStatusCode.NOT_FOUND)
                    .setBodyByPath("/404.html");
        }

        final User user = possibleUser.get();
        final String password = requestBody.get("password");
        if (!user.checkPassword(password)) {
            log.info("비밀번호 불일치 [요청 account: {}]", account);
            return HttpResponse.init(HttpStatusCode.UNAUTHORIZED)
                    .setBodyByPath("/401.html");
        }

        final Session session = Session.generate();
        session.setAttribute("user", user);
        SessionManager.add(session);

        log.info("로그인 성공 [요청 account: {}]", account);
        return HttpResponse.init(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .addCookie("JSESSIONID", session.getId());
    }
}
