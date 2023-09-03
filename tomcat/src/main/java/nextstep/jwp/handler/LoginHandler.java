package nextstep.jwp.handler;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.FileHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginHandler extends FileHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        String account = httpRequest.getQuery("account");
        String password = httpRequest.getQuery("password");

        Optional<User> found = InMemoryUserRepository.findByAccount(account);
        if (found.isEmpty()) {
            return super.handle(httpRequest);
        }
        User user = found.get();
        if (user.checkPassword(password)) {
            System.out.println("user = " + user);
        }
        return super.handle(httpRequest);
    }
}
