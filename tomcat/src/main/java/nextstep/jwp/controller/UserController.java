package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.Request;

public class UserController {

    public String doGet(final Request request) {
        if (request.isSameRequestUrl("/login")) {
            String account = request.getQueryParameters().get("account");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(UserNotFoundException::new);

            return user.toString();
        }
        return null;
    }
}
