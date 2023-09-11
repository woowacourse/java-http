package org.apache.coyote.http11.controller;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.controller.support.FileFinder;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.ContentType;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;

public class LoginController extends AbstractController {

    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String LOGIN_PAGE = "/login.html";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final RequestBody body = request.getBody();
        final Optional<User> user = InMemoryUserRepository.findByAccount(body.getAccount());
        if (user.isPresent() && user.get().checkPassword(body.getPassword())) {
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user.get());
            SessionManager.add(session);
            response.setStatusCode(StatusCode.REDIRECT);
            response.addHeader(LOCATION, INDEX_PAGE);
            response.addHeader(SET_COOKIE, "JSESSIONID=" + session.getId());
            return;
        }
        response.setStatusCode(StatusCode.REDIRECT);
        response.addHeader(LOCATION, UNAUTHORIZED_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (SessionManager.findSession(request.getAuthCookie()) == null) {
            final String body = FileFinder.find(LOGIN_PAGE);
            response.setStatusCode(StatusCode.OK);
            response.addHeader(CONTENT_TYPE, ContentType.HTML.getValue() + CHARSET_UTF_8);
            response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
            response.setBody(body);
            return;
        }
        response.setStatusCode(StatusCode.REDIRECT);
        response.addHeader(LOCATION, INDEX_PAGE);
    }
}
