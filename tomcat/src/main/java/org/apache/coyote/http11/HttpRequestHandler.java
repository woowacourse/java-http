package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final String MAIN_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final HttpRequest httpRequest;

    public HttpRequestHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponseEntity handle() {
        final HttpRequestBody requestBody = httpRequest.getBody();

        if (httpRequest.isLogin()) {
            if (httpRequest.isCorrectMethod(HttpMethod.GET)) {
                return new HttpResponseEntity(HttpStatus.OK, LOGIN_PAGE);
            }
            return login(requestBody);
        }

        if (httpRequest.isRegister()) {
            if (httpRequest.isCorrectMethod(HttpMethod.GET)) {
                return new HttpResponseEntity(HttpStatus.OK, REGISTER_PAGE);
            }

            final Map<String, String> userInfos = requestBody.parseUserInfos();
            if (httpRequest.isCorrectMethod(HttpMethod.POST) &&
                    InMemoryUserRepository.isNonExistentByAccount(userInfos.get("account"))) {
                return register(userInfos);
            }
        }
        return new HttpResponseEntity(HttpStatus.OK, httpRequest.getPath().getPath());
    }

    private HttpResponseEntity login(final HttpRequestBody requestBody) {
        final Map<String, String> userInfos = requestBody.parseUserInfos();
        final String account = userInfos.get("account");
        final String password = userInfos.get("password");
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        if (user.checkPassword(password)) {
            final SessionManager sessionManager = new SessionManager();
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);

            log.info("로그인 성공! 아이디: {}", account);
            final HttpResponseEntity responseEntity = new HttpResponseEntity(HttpStatus.FOUND, MAIN_PAGE, true);
            responseEntity.addCookie(HttpCookie.ofJSessionId(session.getId()));
            return responseEntity;
        }
        return new HttpResponseEntity(HttpStatus.UNAUTHORIZED, "/401.html", true);
    }

    private HttpResponseEntity register(final Map<String, String> userInfos) {
        final User user = new User(userInfos.get("account"), userInfos.get("password"), userInfos.get("email"));
        InMemoryUserRepository.save(user);
        return new HttpResponseEntity(HttpStatus.FOUND, MAIN_PAGE, true);
    }
}
