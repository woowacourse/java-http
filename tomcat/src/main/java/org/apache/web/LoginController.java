package org.apache.web;

import com.techcourse.db.InMemoryUserRepository;
import java.util.Map;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public Http11Response control(final Http11Request request) {
        final Map<String, String> params = request.extractRequestBody();
        final String account = params.get("account");
        final String passwor = params.get("password");

        final boolean loginSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(passwor))
                .isPresent();

        if (loginSuccess) {
            log.info("로그인 성공 - account: {}", account);
            return Http11Response.redirect("/index.html");
        }
        log.warn("로그인 실패 - account: {}", account);
        return Http11Response.redirect("/401.html");
    }
}
