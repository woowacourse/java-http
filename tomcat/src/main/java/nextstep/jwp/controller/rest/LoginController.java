package nextstep.jwp.controller.rest;

import static nextstep.jwp.controller.StaticResourceResolver.HOME_PAGE;
import static nextstep.jwp.controller.StaticResourceResolver.LOGIN_PAGE;
import static nextstep.jwp.controller.StaticResourceResolver.UNAUTHORIZED_PAGE;

import java.util.NoSuchElementException;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ResponseEntity;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatusCode;

public class LoginController implements Controller {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getPath().equals("/login");
    }

    @Override
    public ResponseEntity handle(HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        return doPost(request);
    }

    private ResponseEntity doGet(HttpRequest request) {
        return ResponseEntity.forward(HttpStatusCode.OK, LOGIN_PAGE);
    }

    private ResponseEntity doPost(HttpRequest request) {
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
