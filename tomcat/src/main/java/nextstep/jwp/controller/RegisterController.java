package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends Controller {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.ok().fileBody("/register.html").build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final String account = request.getRequestParam("account");
        final String password = request.getRequestParam("password");
        final String email = request.getRequestParam("email");

        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

        if (userOptional.isPresent()) {
            return HttpResponse.redirect("/register").build();
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.redirect("/index.html").build();
    }
}
