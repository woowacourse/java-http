package nextstep.jwp.web.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.framework.domain.annotation.Controller;
import nextstep.jwp.framework.domain.annotation.PostMapping;
import nextstep.jwp.model.User;

@Controller
public class UserController {

    @PostMapping("/login")
    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("해당 계정의 유저가 없습니다."));
        if (user.checkPassword(password)) {
            return user;
        }
        throw new IllegalStateException("비밀번호가 틀렸습니다.");
    }
}
