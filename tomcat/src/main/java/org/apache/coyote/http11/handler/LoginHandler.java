package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.request.HttpRequestMethod.GET;
import static org.apache.coyote.http11.request.HttpRequestMethod.POST;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;
import static org.apache.coyote.http11.response.HttpStatusCode.UNAUTHORIZED;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManger;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginHandler extends RequestHandler {

    public LoginHandler(final SessionManger sessionManager) {
        super(sessionManager);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        try {
            String sessionId = httpRequest.getSessionId();
            if (sessionId != null && sessionManager.findSession(sessionId) != null) {
                return getRedirectPage(httpRequest, DEFAULT_RESOURCE, FOUND);
            }

            final HttpRequestMethod httpMethod = httpRequest.getMethod();

            if (httpMethod == GET) {
                return getPage(httpRequest, DIRECTORY + LOGIN_RESOURCE, OK);
            }

            if (httpMethod == POST) {
                final HttpRequestBody requestBody = httpRequest.getBody();
                final Map<String, String> accountInfo = parseRequestBody(requestBody.getBody());

                final User user = InMemoryUserRepository.findByAccount(accountInfo.get(ACCOUNT_KEY))
                        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

                if (user.checkPassword(accountInfo.get(PASSWORD_KEY))) {
                    Session newSession = new Session();
                    newSession.setAttribute("user", user);
                    sessionManager.add(newSession);

                    return getRedirectPageForLogin(httpRequest, DEFAULT_RESOURCE, FOUND, newSession);
                }
            }
            return getPage(httpRequest, DIRECTORY + UNAUTHORIZED_RESOURCE, UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            return getNotFoundPage(httpRequest);
        }
    }
}
