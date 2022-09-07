package org.apache.coyote.http11.response.generator;

import static org.apache.coyote.http11.request.HttpMethod.POST;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginResponseGenerator implements ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(LoginResponseGenerator.class);

    private static final String LOGIN_REQUEST = "/login";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String LOGIN_FAILURE_REDIRECT_URI = "http://localhost:8080/401.html";
    private static final String JSESSION_COOKIE_KEY = "JSESSIONID";

    private final Manager sessionManager;

    public LoginResponseGenerator(Manager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf(LOGIN_REQUEST) && httpRequest.hasHttpMethodOf(POST);
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) {
        String account = httpRequest.getParamValueOf(ACCOUNT_KEY);
        String password = httpRequest.getParamValueOf(PASSWORD_KEY);

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (isInvalidUserInfo(user, password)) {
            return HttpResponse.found(LOGIN_FAILURE_REDIRECT_URI);
        }
        return getSuccessResponse(user.get(), httpRequest);
    }

    private boolean isInvalidUserInfo(Optional<User> user, String password) {
        return user.isEmpty() ||
                !user.get()
                        .checkPassword(password);
    }

    private HttpResponse getSuccessResponse(User user, HttpRequest httpRequest) {
        log.info(user.toString());

        if (!httpRequest.hasCookieOf(JSESSION_COOKIE_KEY)) {
            Session session = new Session("user", user);
            sessionManager.add(session);
            return HttpResponse.found(LOGIN_SUCCESS_REDIRECT_URI,
                    Cookie.fromResponse(JSESSION_COOKIE_KEY, session.getId()));
        }
        return HttpResponse.found(LOGIN_SUCCESS_REDIRECT_URI);
    }
}
