package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request, HttpResponseBuilder responseBuilder) {
        return responseBuilder.redirect("register.html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request, HttpResponseBuilder responseBuilder) {
        try {
            User user = toUser(request.body());
            InMemoryUserRepository.save(user);
            return responseBuilder.redirect("/index.html");
        } catch (IllegalArgumentException error) {
            return responseBuilder.redirect("/400.html");
        }
    }

    private User toUser(RequestBody requestBody) {
        Map<String, String> form = requestBody.toApplicationForm();
        return new User(form.get("account"), form.get("password"), form.get("email"));
    }
}
