package nextstep.jwp.controller;

import static nextstep.servlet.StaticResourceResolver.HOME_PAGE;
import static nextstep.servlet.StaticResourceResolver.REGISTER_PAGE;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.message.HttpStatusCode;
import org.apache.coyote.http11.message.request.HttpRequest;

public class RegisterController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.isPathMatch("/register");
    }

    @Override
    ResponseEntity doGet() {
        return ResponseEntity.forward(HttpStatusCode.OK, REGISTER_PAGE);
    }

    @Override
    ResponseEntity doPost(HttpRequest request) {
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
