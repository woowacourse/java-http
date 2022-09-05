package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.controller.dto.UserRegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.SimpleHttpResponse;

public class UserCreateController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest httpRequest, final SimpleHttpResponse httpResponse) {
        UserRegisterRequest request = UserRegisterRequest.from(httpRequest.getParameters());

        Optional<User> user = InMemoryUserRepository.findByAccount(request.getAccount());
        if (user.isPresent()) {
            throw new InvalidUserException("이미 존재하는 계정입니다.");
        }
        InMemoryUserRepository.save(request.toDomain());
        httpResponse.redirect("/index.html");
    }
}
