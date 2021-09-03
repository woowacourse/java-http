package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.util.Map;

public class UserService {
    private UserService() {
    }

    public static User findUser(final Map<String, String> params) {
        String account = getParamByName(params, "account", "계정이 존재하지 않습니다.");
        String password = getParamByName(params, "password", "비밀번호가 존재하지 않습니다.");
        User userByAccount = findUserByAccount(account);
        if (!userByAccount.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        return userByAccount;
    }

    public static User registerUser(final Map<String, String> params) {
        String account = getParamByName(params, "account", "계정이 존재하지 않습니다.");
        String password = getParamByName(params, "password", "비밀번호가 존재하지 않습니다.");
        String email = getParamByName(params, "email", "이메일이 존재하지 않습니다.");
        User user = new User(0L, account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }

    private static User findUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    private static String getParamByName(Map<String, String> params, String name, String message) {
        return params.entrySet().stream()
                .filter(param -> param.getKey().equals(name))
                .map(Map.Entry::getValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(message));
    }
}
