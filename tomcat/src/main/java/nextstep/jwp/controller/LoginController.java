package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
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
            if (!cookie.hasCookie(SESSION_COOKIE_NAME)) {
                String jSessionId = UUID.randomUUID().toString();
                response.addCookie(SESSION_COOKIE_NAME, jSessionId);
                response.statusCode(HttpStatus.REDIRECT);
                response.addHeader("Location", "/index.html");
                Session.put(jSessionId, userByAccount.get());
                return;
            }
        }

        response.loadResource("/401.html");
        response.statusCode(HttpStatus.REDIRECT);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getCookies().getCookie("JSESSIONID") != null) {
            response.statusCode(HttpStatus.REDIRECT);
            response.addHeader("Location", "/index.html");
            return;
        }

        response.loadResource("/login.html");
    }
}
