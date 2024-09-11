package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.ResourceReader;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.MimeType;

public class SignupRequestHandler extends AbstractRequestHandler {

    private static final String REDIRECTION_PATH = "/index.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String USER_SESSION_ATTRIBUTE_NAME = "user";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String body = ResourceReader.readFile(httpRequest.getRequestURI());
        httpResponse.ok(MimeType.HTML, body, StandardCharsets.UTF_8);
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> param = httpRequest.getParsedBody();
        User newUser = new User(param.get(ACCOUNT_KEY), param.get(PASSWORD_KEY), param.get(EMAIL_KEY));
        validateExists(newUser);
        InMemoryUserRepository.save(newUser);
        httpResponse.setSession(getSession(httpRequest, newUser));
        httpResponse.found(REDIRECTION_PATH);
    }

    private void validateExists(User user) {
        if (InMemoryUserRepository.existsUser(user)) {
            throw new UncheckedServletException(new IllegalStateException("이미 존재하는 아이디 입니다."));
        }
    }

    private Session getSession(HttpRequest httpRequest, User newUser) {
        Session session = httpRequest.getSession();
        session.setAttribute(USER_SESSION_ATTRIBUTE_NAME, newUser);
        SessionManager.getInstance().add(session);
        return session;
    }
}
