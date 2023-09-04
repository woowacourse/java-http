package nextstep.jwp.handler;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.FileHandler;
import org.apache.coyote.http11.FormData;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginHandler extends FileHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (httpRequest.getMethod().equals("POST")) {
            FormData formData = FormData.of(httpRequest.getBody());
            String account = formData.get("account");
            String password = formData.get("password");

            Optional<User> found = InMemoryUserRepository.findByAccount(account);
            if (found.isEmpty()) {
                return HttpResponse.redirectTo("/401");
            }
            User user = found.get();
            if (user.checkPassword(password)) {
                return HttpResponse.redirectTo("/index");
            }
            return HttpResponse.redirectTo("/401");
        }
        return super.handle(httpRequest);
    }
}
