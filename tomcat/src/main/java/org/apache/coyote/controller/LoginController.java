package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.handler.SessionManager;
import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;
import org.apache.coyote.http11.http.message.HttpSession;
import org.apache.coyote.http11.http.util.HttpMethod;
import org.apache.coyote.http11.http.util.HttpResponseMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String PASSWORD = "password";
    private static final String JSESSONID = "JSESSIONID= ";

    private final SessionManager sessionManager;

    public LoginController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpResponse run(final HttpRequest request) {
        final String method = request.getMethod();
        if (HttpMethod.GET.isSameMethod(method)) {
            return getLoginPage(request);
        }
        if (HttpMethod.POST.isSameMethod(method)) {
            return login(request);
        }
        throw new IllegalArgumentException("잘못된 메소드 형식입니다.");
    }

    private HttpResponse getLoginPage(final HttpRequest request) {
        if (request.containsCookie()) {
            return HttpResponse.ofRedirect(FileResolver.INDEX_HTML);
        }
        return HttpResponse.ofRedirect(FileResolver.LOGIN);
    }

    private HttpResponse login(final HttpRequest request) {
        final Map<String, String> body = request.getBody();
        final Optional<User> user = InMemoryUserRepository.findByAccount(body.get("account"));
        if (user.isPresent() && isValidUser(user.get(), body.get(PASSWORD))) {
            final HttpResponse httpResponse = HttpResponse.ofRedirect(FileResolver.INDEX_HTML);
            final HttpSession newSession = sessionManager.createSession(user.get());
            return httpResponse.addMessageHeader(HttpResponseMessageHeader.SET_COOKIE, JSESSONID + newSession.getId());
        }
        return HttpResponse.ofRedirect(FileResolver.HTML_401);
    }

    private boolean isValidUser(final User user, final String password) {
        if (user.checkPassword(password)) {
            log.info("로그인 성공!\n회원 아이디: {} ", user.getAccount());
            return true;
        }
        return false;
    }
}
