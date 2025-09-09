package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.exception.CommonException;

public class LoginController {

    public void login(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        String account = httpRequest.getForm("account");
        String password = httpRequest.getForm("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new CommonException(HttpStatus.UNAUTHORIZED);
        }

        if (user.get().checkPassword(password)) {
            httpResponse.setStatusCode(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "http://localhost:8080");
            System.out.println(account + " 로그인 완료");
            return;
        }
        throw new CommonException(HttpStatus.UNAUTHORIZED);
    }

    public void register(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {

        String account = httpRequest.getForm("account");
        String email = httpRequest.getForm("email");
        String password = httpRequest.getForm("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        httpResponse.setStatusCode(HttpStatus.FOUND);
        httpResponse.setHeader("Location", "http://localhost:8080");
    }
}
