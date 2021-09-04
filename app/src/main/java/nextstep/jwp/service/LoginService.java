package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.model.User;

public class LoginService {

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                                     .orElseThrow(() -> { throw new LoginException("해당 User가 존재하지 않습니다."); });
    }

    public User login(HttpRequest request) {
        HttpBody httpBody = request.getBody();
        String account = httpBody.getBodyParams("account");
        String password = httpBody.getBodyParams("password");

        User user = findUser(account);
        if (!user.checkPassword(password)) {
            throw new LoginException("User의 정보와 입력한 정보가 일치하지 않습니다.");
        }

        return user;
    }
}
