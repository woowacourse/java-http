package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private AuthController() {
    }

    public static HttpResponse login(HttpRequest request) {

        if (!request.hasQuery()) {
            return new HttpResponse(request, StatusCode.OK, getStaticResource(request.getUrl()));
        }

        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");

        try {
            User user = loginUser(account, password);
            log.info("로그인 성공! 아이디: {}", user.getAccount());
        } catch (IllegalArgumentException e) {
            return HttpResponse.redirect(request, "/401.html");
        }

        HttpResponse response = HttpResponse.redirect(request, "/index.html");
        if (!request.containsHeader("Cookie")) {
            return response;
        }
        response.setCookie(new HttpCookie());
        return response;
    }

    public static HttpResponse signUp(HttpRequest request) {
        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");
        final String email = request.getQueryValue("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! 아이디: {}", user.getAccount());
        return HttpResponse.redirect(request, "/index.html");
    }

    private static String getStaticResource(URL url) {
        try {
            return new String(Files.readAllBytes(new File(Objects.requireNonNull(url)
                .getFile())
                .toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("No such resource");
        }
    }

    private static User loginUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("Account Not Found"));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("Password Not Matched");
        }
        return user;
    }
}
