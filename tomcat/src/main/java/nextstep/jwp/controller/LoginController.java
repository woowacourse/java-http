package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.HttpSession;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final SessionManager sessionManager = new SessionManager();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> bodyParams = request.getBody();
        String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
        String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");

        Optional<User> userByAccount = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        Cookie cookie = request.getCookies();

        if (userByAccount.isPresent()) {
            log.info("로그인 성공!" + " 아이디: " + userByAccount.get().getAccount());
            processLogin(response, userByAccount);
            return;
        }

        response.loadResource("/401.html");
        response.statusCode(HttpStatus.REDIRECT);
    }

    private void processLogin(HttpResponse response, Optional<User> userByAccount) {
        HttpSession session = new HttpSession();
        session.put("user", userByAccount.get());
        String jSessionId = session.getId();
        response.addCookie(SESSION_COOKIE_NAME, jSessionId);
        response.statusCode(HttpStatus.REDIRECT);
        response.addHeader("Location", "/index.html");
        sessionManager.add(session);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String jSessionId = request.getCookies().getCookie("JSESSIONID");

        if (jSessionId != null) {
            processSessionLogin(response, jSessionId);
            return;
        }

        response.loadResource("/login.html");
    }

    private void processSessionLogin(HttpResponse response, String jSessionId) throws IOException {
        HttpSession session = sessionManager.findSession(jSessionId);
        if (session == null || session.get("user") == null) {
            response.loadResource("/login.html");
            return;
        }
        User user = (User) session.get("user");
        log.info("로그인 성공!" + " 아이디: " + user.getAccount());
        response.statusCode(HttpStatus.REDIRECT);
        response.addHeader("Location", "/index.html");
    }
}
