package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;
import org.apache.coyote.http11.request.RequestBody;

public class RegisterController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.isGetRequest()) {
            return handleGetMethod();
        }
        return handlePostMethod(request);
    }

    private HttpResponse handleGetMethod() {
        return HttpResponse.builder()
                .statusCode(StatusCode.OK)
                .contentType(ContentType.TEXT_HTML)
                .responseBody(ViewLoader.from("/register.html"))
                .build();
    }

    private HttpResponse handlePostMethod(final HttpRequest request) {
        if (request.hasRequestBody()) {
            final RequestBody requestBody = request.getRequestBody();
            register(requestBody);
            return HttpResponse.builder()
                    .statusCode(StatusCode.CREATED)
                    .contentType(ContentType.TEXT_HTML)
                    .responseBody(ViewLoader.toIndex())
                    .build();
        }
        return HttpResponse.toNotFound();
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
