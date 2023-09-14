package nextstep.jwp.controller;

import static nextstep.servlet.StaticResourceResolver.HOME_PAGE;
import static nextstep.servlet.StaticResourceResolver.LOGIN_PAGE;
import static nextstep.servlet.StaticResourceResolver.UNAUTHORIZED_PAGE;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpStatusCode;
import org.apache.coyote.http11.message.request.HttpRequest;

public class LoginController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.isPathMatch("/login");
    }

    @Override
    ResponseEntity doGet() {
        return ResponseEntity.forward(HttpStatusCode.OK, LOGIN_PAGE);
    }

    @Override
    ResponseEntity doPost(HttpRequest request) {
        try {
            final User user = InMemoryUserRepository.findByAccount(request.getJsonProperty("account"))
                                                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 계정입니다."));
            if (user.checkPassword(request.getJsonProperty("password"))) {
                final var session = request.getSession();
                final var headers = HttpHeaders.defaultHeaders();
                session.setAttribute("user", user);
                headers.setCookie("JSESSIONID", session.getId());
                headers.put(HttpHeaders.LOCATION, HOME_PAGE);
                return new ResponseEntity(HttpStatusCode.FOUND, headers, "", true);
            }
            return ResponseEntity.found(UNAUTHORIZED_PAGE);
        } catch (NoSuchElementException e) {
            return ResponseEntity.found(UNAUTHORIZED_PAGE);
        }
    }
}
