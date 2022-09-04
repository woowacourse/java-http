package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

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
            log.info("user : {}", user);
        } catch (IllegalArgumentException e) {
            return HttpResponse.redirect(request, "/401.html");
        }
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
