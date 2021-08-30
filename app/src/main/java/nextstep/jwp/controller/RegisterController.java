package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.FileReaderInStaticFolder;
import nextstep.jwp.model.User;

public class RegisterController implements Controller {

    private static final String REGISTER_URI = "/register";

    @Override
    public void get(HttpRequest request, HttpResponse response) {
        FileReaderInStaticFolder fileReaderInStaticFolder = new FileReaderInStaticFolder();
        String htmlOfRegister = fileReaderInStaticFolder.read("register.html");
        response.setStatus(HttpStatus.OK);
        response.setBody(htmlOfRegister);
    }

    @Override
    public void post(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = request.extractFormData();
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        if (Objects.nonNull(account) && Objects.nonNull(password) && Objects.nonNull(email)) {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            response.setStatus(HttpStatus.FOUND);
            response.putHeader("Location", "/index.html");
            return;
        }
        response.setStatus(HttpStatus.BAD_REQUEST);
    }

    @Override
    public boolean isSatisfiedBy(String httpUriPath) {
        return httpUriPath.equals(REGISTER_URI);
    }
}
