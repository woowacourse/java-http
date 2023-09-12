package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.util.RequestBodyParser;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.RequestBody;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        saveUser(request.getRequestBody());
        response.setHttpStatus(HttpStatus.CREATED);
        response.setLocationHeader("/index.html");
    }

    private void saveUser(final RequestBody requestBody) {
        final User user = RequestBodyParser.parse(requestBody);
        InMemoryUserRepository.save(user);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String resourcePath = request.getPath() + ".html";
        response.setHttpStatus(HttpStatus.OK);
        response.setContent(resourcePath);
    }
}
