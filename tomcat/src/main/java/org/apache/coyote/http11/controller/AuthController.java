package org.apache.coyote.http11.controller;

import javassist.NotFoundException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.apache.coyote.http11.common.HttpHeaderType.SET_COOKIE;

public class AuthController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String[] splitBody = request.getBody().split("&");
        Optional<String> account = getValueOf("account", splitBody);
        Optional<String> password = getValueOf("password", splitBody);

        if (account.isEmpty() || password.isEmpty()) {
            response.setHttpStatus(HttpStatus.NOT_FOUND).setResponseFileName("/401.html");
            return;
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(account.get());
        if (findUser.isEmpty()) {
            throw new NotFoundException("존재하지 않는 계정입니다.");
        }
        User user = findUser.get();
        if (user.checkPassword(password.get())) {
            log.info(user.toString());
            Session savedSession = sessionManager.add(user);
            response.setHttpStatus(HttpStatus.FOUND).setResponseFileName("/index.html");
            response.addHeader(SET_COOKIE.getValue(), "JSESSIONID=" + savedSession.getId());
            return;
        }
        response.setHttpStatus(HttpStatus.UNAUTHORIZED).setResponseFileName("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<String> sessionId = request.sessionId();
        if (sessionId.isEmpty()) {
            response.setHttpStatus(HttpStatus.OK).setResponseFileName("/login.html");
            return;
        }
        Session session = sessionManager.findSession(sessionId.get());
        User user = (User) session.getAttribute("user");
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
            response.setHttpStatus(HttpStatus.FOUND).setResponseFileName("/index.html");
            return;
        }
        response.setHttpStatus(HttpStatus.OK).setResponseFileName("/login.html");
    }
}
