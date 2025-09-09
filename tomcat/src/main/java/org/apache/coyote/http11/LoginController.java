package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.exception.CommonException;

public class LoginController {

    public void login(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws IOException {
        Map<String, String> query = httpRequest.getQuery();

        String account = query.get("account");
        String password = query.get("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new CommonException(HttpStatus.UNAUTHORIZED);
        }

        if (user.get().checkPassword(password)) {
            httpResponse.setStatusCode(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "http://localhost:8080");
            return;
        }
        throw new CommonException(HttpStatus.UNAUTHORIZED);
    }
}
