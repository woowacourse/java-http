package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.WrongInputException;
import nextstep.jwp.model.User;

public class UserService {

    private static final UserService INSTANCE = new UserService();

    public static UserService getINSTANCE() {
        return INSTANCE;
    }

    public User login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("계정을 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("잘못된 비밀번호입니다.");
        }

        return user;
    }

    public void register(final String account, final String password, final String email) {
        checkUniqueness(account);
        checkEmail(email);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private void checkUniqueness(final String account) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new WrongInputException("이미 존재하는 아이디입니다.");
        }
    }

    private void checkEmail(final String email) {
        if (!email.contains("@")) {
            throw new WrongInputException("이메일 형식이 올바르지 않습니다.");
        }
    }

    private UserService() {
    }
}
