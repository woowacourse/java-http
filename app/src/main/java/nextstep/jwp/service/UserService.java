package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.util.List;

public class UserService {

    public static User findUser(final List<String> params) {
        String account = getAccount(params);
        String password = getPassword(params);
        User userByAccount = findUserByAccount(account);
        if (!userByAccount.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        return userByAccount;
    }

    public static User registerUser(final List<String> params) {
        String account = getAccount(params);
        String password = getPassword(params);
        String email = getEmail(params);
        User user = new User(InMemoryUserRepository.id++, account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }

    private static User findUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    private static String getPassword(final List<String> params) {
        return params.stream()
                .filter(param -> param.contains("password"))
                .map(param -> param.split("=")[1])
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("비밀번호가 존재하지 않습니다."));
    }

    private static String getAccount(final List<String> params) {
        return params.stream()
                .filter(param -> param.contains("account"))
                .map(param -> param.split("=")[1])
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
    }

    private static String getEmail(final List<String> params) {
        return params.stream()
                .filter(param -> param.contains("email"))
                .map(param -> param.split("=")[1])
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
    }
}
