package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;
import nextstep.jwp.HttpStatus;
import nextstep.jwp.StaticFileReader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController implements Controller {

    @Override
    public HttpResponse get(HttpRequest request) {
        StaticFileReader staticFileReader = new StaticFileReader();
        String htmlOfRegister = staticFileReader.read("static/register.html");
        return new HttpResponse(HttpStatus.OK, htmlOfRegister);
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        Map<String, String> formData = request.extractFormData();
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        if (Objects.nonNull(account) && Objects.nonNull(password) && Objects.nonNull(email)) {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
            httpResponse.putHeader("Location", "/index.html");
            return httpResponse;
        }
        return new HttpResponse(HttpStatus.BAD_REQUEST);
    }
}
