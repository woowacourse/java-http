package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryString;
import org.apache.coyote.http11.response.FormData;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String RESOURCE_PATH = "static/login.html";
    private static final String INDEX_URI = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";
    private static final Manager sessionManager = SessionManager.getInstance();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        FormData formData = FormData.from(request.getHttpBody());
        User user = login(formData.get(ACCOUNT), formData.get(PASSWORD));

        Session session = new Session(UUID.randomUUID().toString());
        updateSession(user, session);

        response.setStatus(HttpStatus.FOUND);
        response.setLocation(INDEX_URI);
        response.setCookie(JSESSIONID, Cookie.createWithEmptyAttribute(session.getId()));
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.hasCookie()) {
            User user = loginForCookie(request);

            log.info("로그인 성공 ! 아이디 : {}", user.getAccount());

            response.setStatus(HttpStatus.FOUND);
            response.setLocation(INDEX_URI);

            return;
        }

        if (request.hasQueryString()) {
            QueryString queryString = request.getQueryString();
            User user = login(queryString.get(ACCOUNT), queryString.get(PASSWORD));

            Session session = new Session(UUID.randomUUID().toString());
            updateSession(user, session);

            response.setStatus(HttpStatus.FOUND);
            response.setLocation(INDEX_URI);
            response.setCookie(JSESSIONID, Cookie.createWithEmptyAttribute(session.getId()));

            return;
        }

        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH);

        response.setContentType(ContentType.extractValueFromPath(request.getNativePath()));
        response.setBody(new String(Files.readAllBytes(Path.of(url.getPath()))));
    }

    private void updateSession(User user, Session session) {
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    private User loginForCookie(HttpRequest request) {
        Cookie jSessionCookie = request.getCookie(JSESSIONID);
        Session session = sessionManager.findSession(jSessionCookie.getValue());

        if (session == null) {
            throw new UnauthorizedException("로그인에 실패했습니다. 유효하지 않은 세션입니다.");
        }

        return (User) session.getAttribute("user");
    }

    private User login(String account, String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                log.info("로그인 성공 ! 아이디 : {}", user.getAccount());
                return user;
            }
        }

        throw new UnauthorizedException("로그인에 실패했습니다. account : " + account + " password : " + password);
    }

}
