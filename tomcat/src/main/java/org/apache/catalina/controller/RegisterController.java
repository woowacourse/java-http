package org.apache.catalina.controller;

import static java.util.Objects.requireNonNull;
import static org.apache.catalina.controller.StaticResourceUri.DEFAULT_PAGE;
import static org.apache.catalina.controller.StaticResourceUri.REGISTER_PAGE;
import static org.apache.coyote.http11.response.ResponseContentType.TEXT_HTML;

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

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpRequestBody requestBody = request.getBody();
        final User user = new User(requestBody.parse());

        if (notExistSession(request) && notExistAccount(user)) {
            InMemoryUserRepository.save(user);
        }
        redirectToDefaultPage(response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (notExistSession(request)) {
            final String resource = FileLoader.load(RESOURCE_DIRECTORY + REGISTER_PAGE.getUri());

            response.setStatusCode(HttpStatusCode.OK)
                    .addContentTypeHeader(TEXT_HTML.getType())
                    .addContentLengthHeader(requireNonNull(resource).getBytes().length)
                    .setResponseBody(new HttpResponseBody(resource));
            return;
        }
        redirectToDefaultPage(response);
    }

    private boolean notExistSession(final HttpRequest request) {
        final Session session = Authorizer.findSession(request);
        return session == null;
    }

    private boolean notExistAccount(final User user) {
        return InMemoryUserRepository
                .findByAccount(user.getAccount())
                .isEmpty();
    }

    private void redirectToDefaultPage(final HttpResponse response) {
        response.setStatusCode(HttpStatusCode.FOUND)
                .addContentTypeHeader(TEXT_HTML.getType())
                .addLocationHeader(DEFAULT_PAGE.getUri());
    }
}
