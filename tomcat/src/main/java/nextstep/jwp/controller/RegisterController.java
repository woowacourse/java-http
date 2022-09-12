package nextstep.jwp.controller;

import static org.apache.coyote.http11.handler.StaticResourceHandler.readFile;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final String account = request.getRequestBodyValue("account");
        final String password = request.getRequestBodyValue("password");
        final String email = request.getRequestBodyValue("email");
        InMemoryUserRepository.save(new User(account, password, email));

        return HttpResponse.found(request.getHttpVersion(), request.getContentType(), "/index.html");
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return HttpResponse.ok(request.getHttpVersion(), request.getContentType(), readFile(request.getHttpPath()));
    }
}
