package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.CustomException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.FileReaderInStaticFolder;
import nextstep.jwp.model.User;

public class LoginController implements Controller {

    private static final String LOGIN_URI = "/login";

    @Override
    public HttpResponse get(HttpRequest request) {
        FileReaderInStaticFolder fileReaderInStaticFolder = new FileReaderInStaticFolder();
        String htmlOfLogin = fileReaderInStaticFolder.read("login.html");
        return new HttpResponse(HttpStatus.OK, htmlOfLogin);
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        FileReaderInStaticFolder fileReaderInStaticFolder = new FileReaderInStaticFolder();
        Map<String, String> formData = request.extractFormData();
        String account = formData.get("account");
        String password = formData.get("password");
        if (Objects.isNull(account) || Objects.isNull(password)) {
            return new HttpResponse(HttpStatus.BAD_REQUEST);
        }
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new CustomException("존재하지 않는 아이디입니다."));
        if (user.checkPassword(password)) {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
            httpResponse.putHeader("Location", "/index.html");
            return httpResponse;
        }
        String htmlOf401 = fileReaderInStaticFolder.read("401.html");
        return new HttpResponse(HttpStatus.UNAUTHORIZED, htmlOf401);
    }

    @Override
    public boolean isSatisfiedBy(String httpUriPath) {
        return httpUriPath.equals(LOGIN_URI);
    }
}
