package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.ResourceLoadingException;
import org.apache.coyote.http11.parser.FormDataParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            final HttpResponse newResponse =
                StaticResourceResponseSupplier.getWithExtensionContentType("/register.html");
            response.update(newResponse);
        } catch (IOException e) {
            throw new ResourceLoadingException("/register.html");
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final Map<String, String> formData = FormDataParser.parse(request.getBody());
        final String account = formData.get("account");
        final String email = formData.get("email");
        final String password = formData.get("password");

        InMemoryUserRepository.save(new User(account, password, email));

        response.update(HttpResponse.redirect("/index.html"));
    }
}
