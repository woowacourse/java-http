package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.LoginService;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.Session;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController extends AbstractController {

    private static final String REDIRECT_LOCATION = "/index.html";
    private static final String UNAUTHORIZED_LOCATION = "/401.html";

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final LoginService loginService = new LoginService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasCookieWithSession()) {
            alreadyLoggedIn(request, response);
        }
        try {
            Path path = request.getPath();
            generateStaticResponse(path.getUri() + MimeType.HTML.getExtension(), HttpStatus.OK, response);
        } catch (NullPointerException e) {
            new NotFoundController().doGet(request, response);
        } catch (IOException e) {
            new InternalServerErrorController().doGet(request, response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasCookieWithSession()) {
            alreadyLoggedIn(request, response);
        }
        try {
            Path path = request.getPath();
            if (login(request, response)) {
                generateStaticResponse(path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND, response);
                return;
            }
            generateStaticResponse(UNAUTHORIZED_LOCATION, HttpStatus.UNAUTHORIZED, response);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            doGet(request, response);
        } catch (NullPointerException e) {
            new NotFoundController().doGet(request, response);
        } catch (IOException e) {
            new InternalServerErrorController().doGet(request, response);
        }
    }

    private void alreadyLoggedIn(HttpRequest request, HttpResponse response) throws IOException, NullPointerException {
        Path path = request.getPath();

        generateStaticResponse(path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND, response);
        response.setRedirectLocation(REDIRECT_LOCATION);
    }

    private boolean login(HttpRequest request, HttpResponse response) {
        try {
            User user = loginService.login(request.getBody());
            response.setRedirectLocation(REDIRECT_LOCATION);
            HttpCookie cookie = getHttpCookie(user);
            response.setCookie(cookie.toCookieResponse());
            return true;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private HttpCookie getHttpCookie(User user) {
        Session session = Session.getSession();
        session.setUser(user);
        sessionManager.add(session);
        HttpCookie cookie = new HttpCookie();
        cookie.setSessionId(session.getId());
        return cookie;
    }
}
