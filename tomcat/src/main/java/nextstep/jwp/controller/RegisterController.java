package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends AbstractController {

    public void doGet(HttpRequest request, HttpResponse response) {
        response.redirect("register.html");
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", "register.html");
    }

    public void doPost(HttpRequest request, HttpResponse response) {
        try {
            User user = toUser(request.body());
            InMemoryUserRepository.save(user);
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/index.html");
        } catch (IllegalArgumentException error) {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/400.html");
        }
    }

    private User toUser(RequestBody requestBody) {
        Map<String, String> form = requestBody.toApplicationForm();
        return new User(form.get("account"), form.get("password"), form.get("email"));
    }
}
