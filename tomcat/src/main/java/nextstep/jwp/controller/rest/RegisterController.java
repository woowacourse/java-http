package nextstep.jwp.controller.rest;

import static nextstep.jwp.controller.StaticResourceResolver.HOME_PAGE;
import static nextstep.jwp.controller.StaticResourceResolver.REGISTER_PAGE;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ResponseEntity;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatusCode;

public class RegisterController implements Controller {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getPath().equals("/register");
    }

    @Override
    public ResponseEntity handle(HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        return doPost(request);
    }

    private ResponseEntity doGet(HttpRequest request) {
        return ResponseEntity.forward(HttpStatusCode.OK, REGISTER_PAGE);
    }

    private static ResponseEntity doPost(HttpRequest request) {
        final var user = new User(
                request.getJsonProperty("account"),
                request.getJsonProperty("password"),
                request.getJsonProperty("email")
        );
        try {
            InMemoryUserRepository.findByAccount(user.getAccount())
                                  .ifPresentOrElse(
                                          ignored -> {
                                              throw new IllegalArgumentException("이미 존재하는 계정입니다.");
                                          },
                                          () -> InMemoryUserRepository.save(user)
                                  );
            return ResponseEntity.found(HOME_PAGE);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.found(REGISTER_PAGE);
        }
    }
}
