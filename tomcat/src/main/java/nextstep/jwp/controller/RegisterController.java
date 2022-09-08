package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.exception.DuplicateAccountRegisterException;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        register(request.getBody());
        response.addHeader("Location", "/index.html");
        response.write(HttpStatus.FOUND);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        response.write(HttpStatus.OK, "/register.html");
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
