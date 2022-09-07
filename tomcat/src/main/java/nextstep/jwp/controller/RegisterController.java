package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Body;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends Controller {

    @Override
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

    private User toUser(Body body) {
        Map<String, String> form = body.toApplicationForm();
        return new User(form.get("account"), form.get("password"), form.get("email"));
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", "register.html");
    }
}
