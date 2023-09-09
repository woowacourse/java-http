package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.RequestBody;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;
import org.apache.coyote.controller.util.RequestBodyParser;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        saveUser(request.getRequestBody());
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.CREATED)
                .setLocationHeader("/index.html");
    }

    private void saveUser(final RequestBody requestBody) {
        final User user = RequestBodyParser.parse(requestBody);
        InMemoryUserRepository.save(user);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        final String resourcePath = request.getPath() + ".html";
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .setContent(resourcePath);
    }
}
