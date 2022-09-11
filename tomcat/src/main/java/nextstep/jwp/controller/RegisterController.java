package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Location;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        final String account = request.getRequestBodyValue("account");
        final String password = request.getRequestBodyValue("password");
        final String email = request.getRequestBodyValue("email");
        InMemoryUserRepository.save(new User(account, password, email));

        return new HttpResponse(request.getHttpVersion(), HttpStatus.FOUND, new Location("/index.html"),
                request.getContentType(), readFile(request.getHttpPath()));
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return new HttpResponse(request.getHttpVersion(), HttpStatus.OK, Location.empty(), request.getContentType(),
                readFile(request.getHttpPath()));
    }
}
