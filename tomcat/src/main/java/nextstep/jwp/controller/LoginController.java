package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.ContentType;
import nextstep.jwp.http.common.HeaderType;
import nextstep.jwp.http.common.HttpCookie;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryString;
import nextstep.jwp.http.response.FormData;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.security.Session;
import nextstep.jwp.security.SessionManager;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String RESOURCE_PATH = "static/login.html";
    private static final String INDEX_URI = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";
    private static final Manager SESSION_MANAGER = SessionManager.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        FormData formData = FormData.from(request.getHttpBody());
        User user = login(formData.get(ACCOUNT), formData.get(PASSWORD));

        Session session = new Session(UUID.randomUUID().toString());
        updateSession(user, session);

        response.setStatus(HttpStatus.FOUND);
        response.setHeader(HeaderType.LOCATION.getValue(), INDEX_URI);
        response.setCookie(JSESSIONID + "=" + session.getId());
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.hasCookie()) {
            User user = loginForCookie(request);

            LOG.info("로그인 성공 ! 아이디 : {}", user.getAccount());

            response.setStatus(HttpStatus.FOUND);
            response.setHeader(HeaderType.LOCATION.getValue(), INDEX_URI);

            return;
        }

        if (request.hasQueryString()) {
            QueryString queryString = request.getQueryString();
            User user = login(queryString.get(ACCOUNT), queryString.get(PASSWORD));

            Session session = new Session(UUID.randomUUID().toString());
            updateSession(user, session);

            response.setStatus(HttpStatus.FOUND);
            response.setHeader(HeaderType.LOCATION.getValue(), INDEX_URI);
            response.setCookie(JSESSIONID + "=" + session.getId());

            return;
        }

        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH);

        response.setContentType(ContentType.extractValueFromPath(request.getNativePath()));
        response.setBody(new String(Files.readAllBytes(Path.of(url.getPath()))));
    }

    private void updateSession(User user, Session session) {
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
    }

    private User loginForCookie(HttpRequest request) {
        HttpCookie cookie = request.getCookie();
        String jSessionId = cookie.get(JSESSIONID);
        Session session = SESSION_MANAGER.findSession(jSessionId);
        User user = (User) session.getAttribute("user");

        // TODO: 2023/09/07 쿠키 삭제하는 로직 구현 maxAge 등
        if (user == null) {
            throw new UnauthorizedException("로그인에 실패했습니다. 유효하지 않은 세션입니다.");
        }

        return user;
    }

    private User login(String account, String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                LOG.info("로그인 성공 ! 아이디 : {}", user.getAccount());
                return user;
            }
        }

        throw new UnauthorizedException("로그인에 실패했습니다. account : " + account + " password : " + password);
    }

}
