package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginInfoException;
import nextstep.jwp.model.User;

public class LoginController {

    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (user.checkPassword(password)) {
            // TODO : 반환 타입 DTO로 변환하기
            return user;
        }
        throw new InvalidLoginInfoException();
    }
}
