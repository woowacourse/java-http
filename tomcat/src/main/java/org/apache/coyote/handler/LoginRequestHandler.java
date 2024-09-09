package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.ResourceReader;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler extends AbstractRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);
    private static final String SUCCESS_LOGIN_REDIRECT_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isExistsSession()) {
            Session session = httpRequest.getSession();
            User user = (User) session.getAttribute("user");
            log.info("세션 로그인 : " + user);
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.setHeader("Location", SUCCESS_LOGIN_REDIRECT_PATH);
            return;
        }
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.setBody(ResourceReader.readFile(httpRequest.getRequestURI()));
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> param = httpRequest.getParsedBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(param.get("account"));
        if (user.isPresent() && user.get().checkPassword(param.get("password"))) {
            log.info("로그인 성공 : " + user.get().getAccount());
            Session session = httpRequest.getSession();
            session.setAttribute("user", user.get());
            SessionManager.getInstance().add(session);
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.setHeader("Set-Cookie", SESSION_ID_COOKIE_NAME + "=" + session.getId());
            httpResponse.setHeader("Location", SUCCESS_LOGIN_REDIRECT_PATH);
            return;
        }
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Location", UNAUTHORIZED_PATH);
    }
}
