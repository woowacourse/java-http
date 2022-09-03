package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.FileReader;

public class LoginController implements Controller {

    @Override
    public HttpResponse doService(HttpRequest request) {

        if (request.isGet()) {
            return doGet();
        }

        if (request.isPost()) {
            return doPost(request);
        }

        return HttpResponse.notFound();
    }

    private HttpResponse doGet() {
        return HttpResponse.ok("/login.html", FileReader.read("/login.html"));
    }

    private HttpResponse doPost(HttpRequest request) {
        Map<String, String> body = request.getBody();
        String account = body.get("account");
        String password = body.get("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return HttpResponse.found("/401.html");
        }

        System.out.println(user.get());
        return HttpResponse.found("/index.html");
    }
}
