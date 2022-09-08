package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.UserLoginRequest;
import nextstep.jwp.dto.UserRegisterRequest;
import nextstep.jwp.model.DuplicateUserException;
import nextstep.jwp.model.User;

public class UserService {

    private UserService() {
    }

    public static UserService getInstance() {
        return new UserService();
    }

    public void save(final UserRegisterRequest userRegisterRequest) {
        validateExistUser(userRegisterRequest.getAccount());
        final User user = new User(userRegisterRequest.getAccount(), userRegisterRequest.getPassword(), userRegisterRequest.getEmail());
        InMemoryUserRepository.save(user);
    }

    private void validateExistUser(final String account) {
        if (InMemoryUserRepository.existByAccount(account)) {
            throw new DuplicateUserException();
        }
    }

    public void login(final UserLoginRequest userLoginRequest) {
        final User user = InMemoryUserRepository.getByAccount(userLoginRequest.getAccount());
        validateUserPassword(userLoginRequest.getPassword(), user);
    }

    private void validateUserPassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 정확하지 않습니다. : " + password);
        }
    }
}
