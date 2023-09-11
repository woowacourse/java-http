package org.apache.catalina.controller;

import static java.util.Objects.requireNonNull;
import static org.apache.catalina.controller.StaticResourceUri.DEFAULT_PAGE;
import static org.apache.catalina.controller.StaticResourceUri.LOGIN_PAGE;
import static org.apache.coyote.http11.response.ResponseContentType.TEXT_HTML;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.util.Authorizer;
import org.apache.catalina.util.FileLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatusCode;

public class LoginController extends AbstractController {

    public static final String USER_ATTRIBUTE_KEY = "user";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        if (existSession(request)) {
            redirectToDefaultPage(response);
            return;
        }

        final HttpRequestBody requestBody = request.getBody();
        final User reqeustUser = new User(requestBody.parse());

        final User user = InMemoryUserRepository.findByAccount(reqeustUser.getAccount())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        if (user.checkPassword(reqeustUser)) {
            final Session newSession = createSession(user);

            response.setStatusCode(HttpStatusCode.FOUND)
                    .addContentTypeHeader(TEXT_HTML.getType())
                    .addLocationHeader(DEFAULT_PAGE.getUri())
                    .addSetCookieHeader("JSESSIONID=" + newSession.getId());
        }
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (existSession(request)) {
            redirectToDefaultPage(response);
            return;
        }

        final String resource = FileLoader.load(RESOURCE_DIRECTORY + LOGIN_PAGE.getUri());

        response.setStatusCode(HttpStatusCode.OK)
                .addContentTypeHeader(TEXT_HTML.getType())
                .addContentLengthHeader(requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }

    private Session createSession(final User user) {
        Session newSession = new Session();
        newSession.setAttribute(USER_ATTRIBUTE_KEY, user);
        Authorizer.addSession(newSession);
        return newSession;
    }

    private boolean existSession(final HttpRequest request) {
        final Session session = Authorizer.findSession(request);
        return session != null;
    }

    private void redirectToDefaultPage(final HttpResponse response) {
        response.setStatusCode(HttpStatusCode.FOUND)
                .addContentTypeHeader(TEXT_HTML.getType())
                .addLocationHeader(DEFAULT_PAGE.getUri());
    }
}
