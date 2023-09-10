package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public class RegisterController extends AbstractController {
    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        final Map<String, String> requestForms = request.getRequestForms().getFormData();
        final String account = requestForms.get("account");
        final String email = requestForms.get("email");
        final String password = requestForms.get("password");
        InMemoryUserRepository.save(new User(account, password, email));

        response.location("index.html");
        response.status(HttpStatus.FOUND);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final Response createdResponse = Response.createByTemplate(HttpStatus.OK, "register.html");
        response.setBy(createdResponse);
    }
}
