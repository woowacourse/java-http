package org.apache.coyote.handle.handler;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.Session;
import org.apache.coyote.common.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);
    private static final String SLASH = File.separator;
    private static final String REGISTER_PAGE = "register.html";
    private static final String REGISTER_SUCCESS_PAGE = "index.html";
    private static final String REGISTER_FAIL_PAGE = "400.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        viewResolver.renderPage(httpResponse, HttpStatus.OK, REGISTER_PAGE);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Map<String, String> body = httpRequest.getBody(ContentType.APPLICATION_JSON);
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);
        final String email = body.get(EMAIL);
        if (account == null || password == null || email == null) {
            log.warn("Account Or Password Or Email Not Exist");
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.setLocation(SLASH + REGISTER_FAIL_PAGE);
            return;
        }

        final Optional<User> findUser = InMemoryUserRepository.findByAccount(body.get(ACCOUNT));
        if (findUser.isPresent()) {
            log.warn("Registered Account");
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.setLocation(SLASH + REGISTER_FAIL_PAGE);
            return;
        }
        final User user = new User(account, password, email);
        registerSuccess(httpResponse, user);
    }

    private void registerSuccess(final HttpResponse httpResponse, final User user) {
        InMemoryUserRepository.save(user);
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpResponse.addCookie(JSESSIONID, session.getId());
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setLocation(SLASH + REGISTER_SUCCESS_PAGE);
    }
}
