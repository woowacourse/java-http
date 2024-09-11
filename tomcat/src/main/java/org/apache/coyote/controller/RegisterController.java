package org.apache.coyote.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpHeaderField;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController implements Controller {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatusCode(HttpStatusCode.OK);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var user = new User(request.getBodyValue("account"),
                request.getBodyValue("password"),
                request.getBodyValue("email"));
        InMemoryUserRepository.save(user);
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        response.putHeader(HttpHeaderField.LOCATION.getValue(), "index.html");
    }
}
