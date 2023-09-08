package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.request.HttpRequestMethod.GET;
import static org.apache.coyote.http11.request.HttpRequestMethod.POST;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManger;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterHandler extends RequestHandler {

    public RegisterHandler(final SessionManger sessionManager) {
        super(sessionManager);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final HttpRequestMethod httpMethod = httpRequest.getStartLine().getHttpMethod();

        if (httpMethod == POST) {
            final HttpRequestBody requestBody = httpRequest.getBody();
            final Map<String, String> body = parseRequestBody(requestBody.getBody());
            final User user = new User(body.get(ACCOUNT_KEY),
                    body.get(PASSWORD_KEY),
                    body.get(EMAIL_KEY));
            InMemoryUserRepository.save(user);
            return getRedirectPage(httpRequest, DEFAULT_RESOURCE, FOUND);
        }

        if (httpMethod == GET) {
            return getPage(httpRequest, DIRECTORY + REGISTER_RESOURCE, OK);
        }

        return getNotFoundPage(httpRequest);
    }
}
