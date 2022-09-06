package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController implements Controller {

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isGet()) {
            return HttpResponse.ok("/register.html");
        }
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }
        return HttpResponse.notFound();
    }

    private HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        Map<String, String> requestBody = httpRequest.getBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        User user = new User(InMemoryUserRepository.getDatabaseSize(), account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.ok("/index.html");
    }
}
