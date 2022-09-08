package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.controller.dto.UserRegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.support.Url;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCreateController extends AbstractController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserCreateController.class);

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        UserRegisterRequest request = UserRegisterRequest.from(httpRequest.getParameters());

        try {
            Optional<User> user = InMemoryUserRepository.findByAccount(request.getAccount());
            if (user.isPresent()) {
                throw new InvalidUserException("이미 존재하는 계정입니다.");
            }
            InMemoryUserRepository.save(request.toDomain());
            httpResponse.redirect(Url.createUrl("/index.html"));
        } catch (InvalidUserException e) {
            LOGGER.error("error", e);
            httpResponse.sendError(HttpStatus.BAD_REQUEST, Url.createUrl("/400.html"));
        } catch (Exception e) {
            LOGGER.error("error", e);
            httpResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR, Url.createUrl("/500.html"));
        }
    }
}
