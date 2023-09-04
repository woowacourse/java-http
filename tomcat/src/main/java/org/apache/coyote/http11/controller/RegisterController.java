package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;

public class RegisterController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.isGetRequest()) {
            return handleGetMethod(request);
        }
        return handlePostMethod(request);
    }

    private HttpResponse handleGetMethod(final HttpRequest request) {
        return new HttpResponse(StatusCode.OK, ContentType.TEXT_HTML.getValue(), ViewLoader.from("/register.html"));
    }

    private HttpResponse handlePostMethod(final HttpRequest request) {
        if (request.hasRequestBody()) {
            final Map<String, String> requestBody = request.getRequestBody();
            register(requestBody);
            return new HttpResponse(StatusCode.CREATED, ContentType.TEXT_HTML.getValue(), ViewLoader.toIndex());
        }
        return HttpResponse.toNotFound();
    }

    private void register(final Map<String, String> requestBody) {
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
