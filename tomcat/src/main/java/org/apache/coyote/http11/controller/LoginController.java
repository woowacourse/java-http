package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final Uri indexUri = Uri.INDEX;
    private static final Uri loginUri = Uri.LOGIN;
    private static final Uri unAuthorizedUri = Uri.UNAUTHORIZED;

    @Override
    public boolean canHandle(final HttpRequest request) {
        final String path = request.getRequestLine().getPath();
        return path.startsWith(loginUri.getSimplePath());
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        if (isLoggedIn(request)) {
            return redirect(indexUri.getFullPath());
        }
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.getParamValue("account");
        final String password = requestBody.getParamValue("password");
        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

        if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
            final User user = userOptional.get();
            log.info("user = {}", user);
            final Session session = request.getSession(true);
            session.setAttribute("user", user);
            return redirectWithSession(indexUri.getFullPath(), session.getId());
        }
        return redirect(unAuthorizedUri.getFullPath());
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        if (isLoggedIn(request)) {
            final Session session = request.getSession(false);
            final User user = (User) session.getAttribute("user");
            log.info("user = {}", user);

            return HttpResponse.redirect(HttpStatus.FOUND, indexUri.getFullPath());
        }
        final String path = request.getRequestLine().getPath();
        final StaticResource staticResource = StaticResource.from(path);
        final ResponseBody responseBody = ResponseBody.from(staticResource);
        return HttpResponse.of(HttpStatus.OK, responseBody);
    }

    private boolean isLoggedIn(final HttpRequest httpRequest) {
        final Optional<Cookie> cookieOptional = httpRequest.getCookieValue("JSESSIONID");
        if (cookieOptional.isPresent() && SessionManager.has(cookieOptional.get().getValue())) {
            final String cookieJsessionId = cookieOptional.get().getValue();
            final Session session = SessionManager.findSession(cookieJsessionId);
            return session.getId().equals(cookieJsessionId);
        }
        return false;
    }

    private HttpResponse redirect(final String path) {
        return HttpResponse.redirect(HttpStatus.FOUND, path);
    }

    private HttpResponse redirectWithSession(final String path, final String sessionId) throws IOException {
        final HttpResponse httpResponse = redirect(path);
        httpResponse.addSession(sessionId);
        return httpResponse;
    }
}
