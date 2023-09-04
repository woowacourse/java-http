package nextstep.jwp.controller.rest;

import static nextstep.jwp.controller.StaticResourceController.HOME_PAGE;
import static nextstep.jwp.controller.StaticResourceController.UNAUTHORIZED_PAGE;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatusCode;

public class LoginController implements RestController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getPath().equals("/login") && request.getMethod() == HttpMethod.POST;
    }

    @Override
    public ResponseEntity handle(HttpRequest request) {
        final var headers = HttpHeaders.defaultHeaders();
        try {
            final User user = InMemoryUserRepository.findByAccount(request.getJsonProperty("account"))
                                                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 계정입니다."));
            if (user.checkPassword(request.getJsonProperty("password"))) {
                final var session = SessionManager.getInstance().create();
                session.setAttribute("user", user);
                headers.setCookie("JSESSIONID", session.getId());
                headers.put(HttpHeaders.LOCATION, HOME_PAGE);
                return new ResponseEntity(HttpStatusCode.FOUND, headers, "");
            }
            return ResponseEntity.found(UNAUTHORIZED_PAGE);
        } catch (NoSuchElementException e) {
            return ResponseEntity.found(UNAUTHORIZED_PAGE);
        }
    }
}
