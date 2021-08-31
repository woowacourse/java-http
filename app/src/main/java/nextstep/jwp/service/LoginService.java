package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.model.User;

public class LoginService {

    public User findUser(RequestBody requestBody) {
        String account = requestBody.getParam("account");
        return InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. 회원가입을 해주세요"));
    }
}
