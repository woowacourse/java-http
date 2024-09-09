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

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public class LoginController extends AbstractController {

    private static final String REDIRECT_LOCATION = "/index.html";
    private static final String UNAUTHORIZED_LOCATION = "/401.html";

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final LoginService loginService = new LoginService();

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        if (request.getCookie().hasCookieName("JSESSIONID")) {
            return alreadyLoggedIn(request);
        }
        try {
            Path path = request.getPath();
            return generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        //TODO: 메서드 물어볼 수 있게 수정
        if (request.getCookie().hasCookieName("JSESSIONID")) {
            return alreadyLoggedIn(request);
        }
        try {
            Path path = request.getPath();

            HttpResponse response = generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND);
            HttpResponse loginFailResponse = generateResponse(STATIC_RESOURCE_LOCATION + UNAUTHORIZED_LOCATION, HttpStatus.UNAUTHORIZED);

            if (login(request, response)) {
                return response;
            }
            return loginFailResponse;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return doGet(request);
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }

    private HttpResponse alreadyLoggedIn(HttpRequest request) throws IOException, NullPointerException {
        Path path = request.getPath();

        HttpResponse response = generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND);
        response.setRedirectLocation(REDIRECT_LOCATION);

        return response;
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
