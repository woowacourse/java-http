package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;
import org.apache.coyote.http11.request.RequestBody;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.hasRequestBody()) {
            final RequestBody requestBody = request.initRequestBody();
            register(requestBody);
            response
                .statusCode(StatusCode.CREATED)
                .contentType(ContentType.TEXT_HTML)
                .responseBody(ViewLoader.toIndex());
            return;
        }
        response
            .statusCode(StatusCode.NOT_FOUND)
            .contentType(ContentType.TEXT_HTML)
            .responseBody(ViewLoader.toNotFound());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response
            .statusCode(StatusCode.OK)
            .contentType(ContentType.TEXT_HTML)
            .responseBody(ViewLoader.from("/register.html"));
    }

    private void register(final RequestBody requestBody) {
        try {
            final String account = requestBody.get("account");
            final String password = requestBody.get("password");
            final String email = requestBody.get("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
        } catch (NullPointerException e) {
            e.getStackTrace();
        }
    }
}
