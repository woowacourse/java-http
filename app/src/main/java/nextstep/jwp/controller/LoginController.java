package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.CustomException;
import nextstep.jwp.http.FileReaderInStaticFolder;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String LOGIN_URI = "/login";

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        FileReaderInStaticFolder fileReaderInStaticFolder = new FileReaderInStaticFolder();
        String htmlOfLogin = fileReaderInStaticFolder.read("login.html");
        response.setStatus(HttpStatus.OK);
        response.setBody(htmlOfLogin);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        FileReaderInStaticFolder fileReaderInStaticFolder = new FileReaderInStaticFolder();
        Map<String, String> formData = request.extractFormData();
        String account = formData.get("account");
        String password = formData.get("password");
        if (Objects.isNull(account) || Objects.isNull(password)) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            return;
        }
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new CustomException("존재하지 않는 아이디입니다."));
        if (user.checkPassword(password)) {
            response.setStatus(HttpStatus.FOUND);
            response.putHeader("Location", "/index.html");
            HttpSession session = request.getSession()
                .orElseThrow(() -> new CustomException("서버에서 세션을 정상적으로 생성하지 않았습니다."));
            session.setAttribute("user", user);
            return;
        }
        String htmlOf401 = fileReaderInStaticFolder.read("401.html");
        response.setStatus(HttpStatus.UNAUTHORIZED);
        response.setBody(htmlOf401);
    }

    @Override
    public boolean isSatisfiedBy(String httpUriPath) {
        return httpUriPath.equals(LOGIN_URI);
    }
}
