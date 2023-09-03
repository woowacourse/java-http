package nextstep.jwp.controller;

import static org.apache.coyote.http11.common.ContentType.TEXT;
import static org.apache.coyote.http11.common.Status.OK;
import static org.apache.coyote.http11.common.Status.UNAUTHORIZED;

import java.util.List;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.response.Response;

public class UserController {

    private UserController() {
    }

    public static Response login(Map<String, List<String>> queryParams) {
        String account = queryParams.get("account").get(0);
        String password = queryParams.get("password").get(0);

        return InMemoryUserRepository.findByAccount(account)
                .filter(loginUser -> loginUser.checkPassword(password))
                .map(loginUser -> Response.of(OK, TEXT.toString(), loginUser.toString()))
                .orElseGet(() -> Response.of(UNAUTHORIZED, TEXT.toString(), ""));
    }
}
