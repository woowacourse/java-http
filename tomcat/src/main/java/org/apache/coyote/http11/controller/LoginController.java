package org.apache.coyote.http11.controller;

import java.util.NoSuchElementException;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.QueryParameters;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        QueryParameters queryParameters = new QueryParameters(request.getRequestBody());

        User user = InMemoryUserRepository.findByAccount(queryParameters.find("account"))
                .orElseThrow(NoSuchElementException::new);

        if (!user.checkPassword(queryParameters.find("password"))) {
            response.addStatusLine(HttpStatus.getStatusCodeAndMessage(302));
            response.addBodyFromFile("/401.html");
            return;
        }

        log.info("user : " + user);
        addCookieToHeader(request, response, user);
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(302));
        response.addBodyFromFile("/index.html");
    }

    private void addCookieToHeader(HttpRequest request, HttpResponse response, User user) {
        if (!request.getRequestHeaders().isExistCookie()) {
            UUID uuid = UUID.randomUUID();
            storeSession(uuid, user);
            response.addHeader("Set-Cookie: JSESSIONID=" + uuid);
        }
    }

    private void storeSession(UUID uuid, User user) {
        Session session = new Session(uuid.toString());
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IllegalAccessException {
        if (isNotNullSession(request)) {
            createIndexPageResponse(response);
            return;
        }
        createLoginPageResponse(request.getPath(), response);
    }

    private boolean isNotNullSession(HttpRequest request) throws IllegalAccessException {
        if (request.getRequestHeaders().isExistCookie()) {
            HttpCookie cookie = request.getRequestHeaders().getCookie();
            String jsessionid = cookie.find("JSESSIONID");
            Session session = sessionManager.findSession(jsessionid);
            return session != null;
        }
        return false;
    }

    private void createIndexPageResponse(HttpResponse response) {
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(302));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile("/index.html");
    }

    private void createLoginPageResponse(String path, HttpResponse response) {
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile(path.concat("." + ContentType.HTML.getExtension()));
    }
}
