package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.NoSuchElementException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session.Session;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;
import org.apache.coyote.http11.model.Headers;
import org.apache.coyote.http11.model.HttpCookie;
import org.apache.coyote.http11.model.Parameters;
import org.apache.coyote.http11.model.Path;
import org.apache.coyote.http11.service.UserService;
import org.apache.coyote.http11.utils.Files;

public final class LoginController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        final Parameters loginParameters = Parameters.parseParameters(request.getRequestBody(), "&");
        final String account = loginParameters.get("account");
        final String password = loginParameters.get("password");

        try {
            final User user = UserService.findUser(account);
            if (user.checkPassword(password)) {
                log.info(user.toString());
                final Session session = Session.create();
                session.setAttribute("user", user);

                return HttpResponse.found("/index.html")
                        .cookie("JSESSIONID=" + session.getId());
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.info(e.getMessage());
        }

        return HttpResponse.found("/401.html");
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String path = Path.from(request.getRequestLine().getPath());

        final Session session = getSession(request);
        if (session != null) {
            final User user = UserService.findUser(session);
            return HttpResponse.found("/index.html");
        }

        final String body = Files.readFile(Path.from(request.getRequestLine().getPath()));
        final String contentType = getContentType(path);

        return HttpResponse.ok(body)
                .contentType(contentType);
    }

    private Session getSession(final HttpRequest request) {
        final String jSessionId = getJSessionId(request);
        if (jSessionId == null) {
            return null;
        }
        return SESSION_MANAGER.findSession(jSessionId);
    }

    private String getJSessionId(final HttpRequest httpRequest) {
        final String cookieHeader = httpRequest.getRequestHeader().get(Headers.COOKIE);
        if (cookieHeader == null) {
            return null;
        }
        final Parameters cookies = Parameters.parseParameters(cookieHeader, ";");
        final HttpCookie httpCookie = HttpCookie.from(cookies);
        return httpCookie.getJSessionId();
    }
}
