package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        if (!"/login".equals(httpRequest.getPath())) {
            return false;
        }
        final Map<String, String> queryStrings = httpRequest.getQueryStrings();
        return queryStrings.containsKey("account") && queryStrings.containsKey("password");
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final Map<String, String> queryStrings = httpRequest.getQueryStrings();
        try {
            final String account = queryStrings.get("account");
            final String password = queryStrings.get("password");
            log.info("login request: id=" + account + ", password=" + password);

            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("그런 회원은 없어요 ~"));
            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("비밀번호가 틀렸어요 ~");
            }

            log.info("login success!");
            Map<String, String> headers = new HashMap<>();
            headers.put("Location", "/index.html");
            return new HttpResponse(
                    "HTTP/1.1",
                    StatusCode.FOUND,
                    headers
            );
        } catch (IllegalArgumentException exception) {
            log.info("login fail!");
            Map<String, String> headers = new HashMap<>();
            headers.put("Location", "/401.html");
            return new HttpResponse(
                    "HTTP/1.1",
                    StatusCode.FOUND,
                    headers
            );
        }
    }
}
