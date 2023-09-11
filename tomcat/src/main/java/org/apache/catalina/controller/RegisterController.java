package org.apache.catalina.controller;

import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseHeaderType.LOCATION;

import java.util.Objects;
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
import org.apache.coyote.http11.response.ResponseContentType;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Session session = Authorizer.findSession(request);
        final HttpRequestBody requestBody = request.getBody();
        final User user = new User(requestBody.parse());

        if (session != null || InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
            response.setStatusCode(HttpStatusCode.FOUND)
                    .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                    .addHeader(LOCATION, StaticResourceUri.DEFAULT_PAGE.getUri());
            return;
        }

        InMemoryUserRepository.save(user);

        response.setStatusCode(HttpStatusCode.FOUND)
                .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                .addHeader(LOCATION, StaticResourceUri.DEFAULT_PAGE.getUri());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = Authorizer.findSession(request);

        if (session != null) {
            response.setStatusCode(HttpStatusCode.FOUND)
                    .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                    .addHeader(LOCATION, StaticResourceUri.DEFAULT_PAGE.getUri());
            return;
        }

        final String resource = FileLoader.load(RESOURCE_DIRECTORY + StaticResourceUri.REGISTER_PAGE.getUri());

        response.setStatusCode(HttpStatusCode.OK)
                .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                .addHeader(CONTENT_LENGTH, Objects.requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }
}
