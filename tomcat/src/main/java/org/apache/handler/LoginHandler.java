package org.apache.handler;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.common.ContentType;
import org.apache.common.Cookie;
import org.apache.common.FileReader;
import org.apache.common.HttpStatus;
import org.apache.common.Session;
import org.apache.common.SessionManager;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
    private static final String JSESSIONID = "JSESSIONID=";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }

        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }
        throw new IllegalArgumentException("일치하는 Method 타입이 없습니다.");
    }

    private HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        String query = httpRequest.getBody();
        String[] queries = query.split("&");
        String account = queries[0].split("=")[1];
        String password = queries[1].split("=")[1];

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            LOG.info("user : {}", user);
            String content = FileReader.read(INDEX_PAGE);

            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, ContentType.TEXT_HTML, content);
            Session session = createSession(httpResponse, user.get());
            httpResponse.setCookie(JSESSIONID + session.getId());
            return httpResponse;
        }

        String content = FileReader.read(UNAUTHORIZED_PAGE);
        return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.TEXT_HTML, content);
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        Cookie cookie = httpRequest.getCookie();
        String sessionId = cookie.getValue("JSESSIONID");
        if (sessionId != null) {
            String content = FileReader.read(INDEX_PAGE);
            return new HttpResponse(HttpStatus.FOUND, ContentType.TEXT_HTML, content);
        }

        String content = FileReader.read(LOGIN_PAGE);
        return new HttpResponse(HttpStatus.OK, ContentType.TEXT_HTML, content);
    }

    private Session createSession(HttpResponse httpResponse, User user) {
        Session session = new Session();
        httpResponse.setCookie(JSESSIONID + session.getId());
        session.setAttribute("user", user);
        SessionManager.add(session);
        return session;
    }
}
