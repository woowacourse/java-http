package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import org.apache.ResourceReader;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler extends AbstractRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);
    private static final String SUCCESS_LOGIN_REDIRECTION_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.existsSession()) {
            Session session = httpRequest.getSession();
            User user = session.getUserAttribute();
            log.info("세션 로그인 : " + user);
            httpResponse.found(SUCCESS_LOGIN_REDIRECTION_PATH);
            return;
        }
        String body = ResourceReader.readFile(httpRequest.getRequestURI());

        httpResponse.ok(MimeType.HTML, body, StandardCharsets.UTF_8);
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> param = httpRequest.getParsedBody();
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(param.get(ACCOUNT_KEY));
        if (userOptional.isPresent() && userOptional.get().checkPassword(param.get(PASSWORD_KEY))) {
            log.info("로그인 성공 : " + userOptional.get());
            Session session = httpRequest.getSession();
            session.setUserAttribute(userOptional.get());
            SessionManager.getInstance().add(session);
            httpResponse.setSession(session);
            httpResponse.found(SUCCESS_LOGIN_REDIRECTION_PATH);
            return;
        }
        httpResponse.found(UNAUTHORIZED_PATH);
    }
}
