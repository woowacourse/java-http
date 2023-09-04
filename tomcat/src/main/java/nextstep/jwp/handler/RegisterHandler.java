package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.FileHandler;
import org.apache.coyote.http11.body.FormData;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterHandler extends FileHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (httpRequest.getMethod().equals("POST")) {
            FormData formData = FormData.of(httpRequest.getBody());

            String account = formData.get("account");
            String password = formData.get("password");
            String email = formData.get("email");

            InMemoryUserRepository.save(new User(account, password, email));
            return HttpResponse.redirectTo("/index");
        }
        return super.handle(httpRequest);
    }
}
