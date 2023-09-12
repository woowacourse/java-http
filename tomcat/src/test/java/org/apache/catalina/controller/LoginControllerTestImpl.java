package org.apache.catalina.controller;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;

public class LoginControllerTestImpl extends AbstractController {

    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final RequestBody body = request.getBody();
        final Optional<User> user = InMemoryUserRepository.findByAccount(body.getAccount());
        if (user.isPresent() && user.get().checkPassword(body.getPassword())) {
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user.get());
            SessionManager.add(session);
            response.setStatusCode(StatusCode.FOUND);
            response.addHeader(LOCATION, INDEX_PAGE);
            response.addHeader(SET_COOKIE, "JSESSIONID=success");
            return;
        }
        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(LOCATION, UNAUTHORIZED_PAGE);
    }
}
