package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.handler.SessionManager;
import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;
import org.apache.coyote.http11.http.message.HttpSession;
import org.apache.coyote.http11.http.util.ReasonPhrase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.LOCATION;
import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.SET_COOKIE;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String PASSWORD = "password";
    private static final String JSESSONID = "JSESSIONID= ";

    private final SessionManager sessionManager;

    public LoginController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.containsSessionId()) {
            response.setReasonPhrase(ReasonPhrase.FOUND);
            response.setMessageHeaders(LOCATION, FILE_PATH_PREFIX + FileResolver.INDEX_HTML.getFileName());
            response.setMessageBody(EMPTY_MESSAGE_BODY);
            return;
        }
        response.setReasonPhrase(ReasonPhrase.FOUND);
        response.setMessageHeaders(LOCATION, FILE_PATH_PREFIX + FileResolver.LOGIN.getFileName());
        response.setMessageBody(EMPTY_MESSAGE_BODY);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> body = request.getBody();
        final Optional<User> user = InMemoryUserRepository.findByAccount(body.get("account"));
        if (user.isPresent() && isValidUser(user.get(), body.get(PASSWORD))) {
            response.setReasonPhrase(ReasonPhrase.FOUND);
            response.setMessageHeaders(LOCATION, FILE_PATH_PREFIX + FileResolver.INDEX_HTML.getFileName());
            final HttpSession newSession = sessionManager.createSession(user.get());
            response.setMessageHeaders(SET_COOKIE, JSESSONID + newSession.getId());
            response.setMessageBody(EMPTY_MESSAGE_BODY);
            return;
        }
        response.setReasonPhrase(ReasonPhrase.FOUND);
        response.setMessageHeaders(LOCATION, FILE_PATH_PREFIX + FileResolver.HTML_401.getFileName());
        response.setMessageBody(EMPTY_MESSAGE_BODY);
    }

    private boolean isValidUser(final User user, final String password) {
        if (user.checkPassword(password)) {
            log.info("로그인 성공!\n회원 아이디: {} ", user.getAccount());
            return true;
        }
        return false;
    }
}
