package org.apache.web;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public Http11Response control(final Http11Request request) {
        final Map<String, String> params = request.extractRequestBodyParams();
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");
        final User userEntity = new User(account, password, email); //user 원시 값 포장 해야 할 것 같음, 다 string 타입이라 순서에 너무 결합되어있음
        InMemoryUserRepository.save(userEntity);

        boolean registerSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.getAccount().equals(account) && user.checkPassword(password))
                .isPresent();

        if (registerSuccess) {
            log.info("회원가입 성공 - account: {}, email: {}", account, email);
            return Http11Response.redirect("/index.html");
        }
        log.info("회원가입 실패  - account: {}, email: {}", account, email);
        return Http11Response.redirect("/401.html");
    }
}
