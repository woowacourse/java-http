package org.apache.web;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.Account;
import com.techcourse.model.Email;
import com.techcourse.model.Password;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public Http11Response doGet(final Http11Request request) {
        try {
            String path = request.extractPath();
            URL url = getClass().getClassLoader().getResource(path);
            String body = Files.readString(Paths.get(url.toURI()));

            return Http11Response.ok("text/html;charset=utf-8", body);
        } catch (IOException | URISyntaxException e) {
            log.error("회원가입 페이지 읽기 실패", e);
            return Http11Response.serverError();
        }
    }

    @Override
    public Http11Response doPost(final Http11Request request) {
        final Map<String, String> params = request.extractRequestBodyParams();
        final Account account = new Account(params.get("account"));
        final Password password = new Password(params.get("password"));
        final Email email = new Email(params.get("email"));
        final User userEntity = new User(account, password, email);
        InMemoryUserRepository.save(userEntity);

        boolean registerSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkAccount(account) && user.checkPassword(password))
                .isPresent();

        if (registerSuccess) {
            log.info("회원가입 성공 - {}", userEntity);
            return Http11Response.redirect("/index.html");
        }
        log.info("회원가입 실패  - {}", userEntity);
        return Http11Response.redirect("/401.html");
    }
}
