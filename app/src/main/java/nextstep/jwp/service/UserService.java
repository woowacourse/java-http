package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.util.List;

public class UserService {
    public static User findUser(List<String> params) {
        User userByAccount = getUserByAccount(params);
        if (!userByAccount.checkPassword(getPassword(params))) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        return userByAccount;
    }

    private static String getPassword(List<String> params) {
        return params.stream()
                .filter(param -> param.contains("password"))
                .map(param -> param.split("=")[1])
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("비밀번호가 존재하지 않습니다."));
    }

    private static User getUserByAccount(List<String> params) {
        return params.stream()
                .filter(param -> param.contains("account"))
                .map(param -> InMemoryUserRepository.findByAccount(param.split("=")[1])
                        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다.")))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
    }
}
