package nextstep.jwp.dashboard;

public class UserService {

    public UserDto login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                                          .orElseThrow();

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }

        return new UserDto(user);
    }
}
