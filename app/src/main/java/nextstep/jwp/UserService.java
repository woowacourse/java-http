package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;

public class UserService {

    public void login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 계정입니다."));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("잘못된 패스워드입니다.");
        }
    }

    public void save(String account, String password, String email) {
        User user = new User(null, account, password, email);
        InMemoryUserRepository.save(user);
    }
}
