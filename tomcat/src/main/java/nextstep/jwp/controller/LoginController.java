package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.db.SessionStorage;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController implements Controller {

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }
        return HttpResponse.notFound();
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        if (httpRequest.containSession()) {
            return HttpResponse.found("/index.html");
        }
        return HttpResponse.ok("/login.html");
    }

    private HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        Map<String, String> requestBody = httpRequest.getBody();

        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if (user.isPresent() && user.get().checkPassword(requestBody.get("password"))) {
            return login(user.get());
        }

        return HttpResponse.unAuthorized();
    }

    private HttpResponse login(User user) throws IOException {
        HttpResponse response = HttpResponse.found("/index.html");
        response.addCookie(SessionStorage.getSession(user.getAccount()));
        return response;
    }
}
