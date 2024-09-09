package com.techcourse.executor;

import com.techcourse.controller.AbstractController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ResourceToResponseConverter;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final String account = request.getBodyAttribute("account");
        final String email = request.getBodyAttribute("email");
        final String password = request.getBodyAttribute("password");

        InMemoryUserRepository.save(new User(account, password, email));
        return ResourceToResponseConverter.convert(HttpStatusCode.FOUND, ResourcesReader.read(Path.from("index.html")));
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return ResourceToResponseConverter.convert(HttpStatusCode.OK, ResourcesReader.read(Path.from("register.html")));
    }
}
