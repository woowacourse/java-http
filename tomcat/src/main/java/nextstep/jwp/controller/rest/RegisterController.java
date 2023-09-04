package nextstep.jwp.controller.rest;

import static nextstep.jwp.controller.StaticResourceController.HOME_PAGE;
import static nextstep.jwp.controller.StaticResourceController.REGISTER_PAGE;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;

public class RegisterController implements RestController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getPath().equals("/register") && request.getMethod() == HttpMethod.POST;
    }

    @Override
    public ResponseEntity handle(HttpRequest request) {
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
