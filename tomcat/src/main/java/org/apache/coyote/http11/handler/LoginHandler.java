package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.request.HttpRequestMethod.GET;
import static org.apache.coyote.http11.request.HttpRequestMethod.POST;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;
import static org.apache.coyote.http11.response.HttpStatusCode.UNAUTHORIZED;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginHandler extends RequestHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        HttpRequestMethod httpMethod = httpRequest.getMethod();
        if (httpMethod == GET) {
            return getPage(httpRequest, DIRECTORY + LOGIN_RESOURCE, OK);
        }

        if (httpMethod == POST) {
            HttpRequestBody requestBody = httpRequest.getBody();
            Map<String, String> accountInfo = parseRequestBody(requestBody.getBody());

            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(accountInfo.get(ACCOUNT_KEY));
            
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (user.checkPassword(accountInfo.get(PASSWORD_KEY))) {
                    return getRedirectPage(httpRequest, DEFAULT_RESOURCE, FOUND);
                }
            }
            return getPage(httpRequest, DIRECTORY + UNAUTHORIZED_RESOURCE, UNAUTHORIZED);
        }
        return getNotFoundPage(httpRequest);
    }
}
