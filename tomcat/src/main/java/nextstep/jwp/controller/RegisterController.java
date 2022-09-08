package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.catalina.exception.DuplicateAccountRegisterException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.Response;

public class RegisterController implements Controller {
    @Override
    public boolean isRunnable(final Request request) {
        return request.hasPath("/register");
    }

    @Override
    public void run(final Request request, final Response response) throws IOException, URISyntaxException {
        if (request.getMethod().equals(HttpMethod.GET)) {
            response.write(HttpStatus.OK, "/register.html");
            return;
        }
        if (request.getMethod().equals(HttpMethod.POST)) {
            register(request.getBody());
            response.addHeader("Location", "/index.html");
            response.write(HttpStatus.FOUND);
        }
    }

    private void register(final RequestBody body) {
        final String account = body.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicateAccountRegisterException();
        }
        final User user = new User(account, body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);
    }
}
