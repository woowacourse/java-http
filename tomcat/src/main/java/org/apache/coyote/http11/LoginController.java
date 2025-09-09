package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.NoSuchElementException;

public class LoginController {

    public void login(
            final HttpRequest httpRequest,
            final HttpResponse httpResponse
    ) {
        Map<String, String> query = httpRequest.getQuery();

        String account = query.get("account");
        String password = query.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(password)) {
            System.out.println("로그인 성공");
        }
    }
}
