package nextstep.jwp.handler.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.dto.RegisterRequest;
import nextstep.jwp.model.User;

public class RegisterService {

    public void register(RegisterRequest registerRequest) {
        User user = registerRequest.toEntity();

        checkIsDuplicatedAccount(user.getAccount());
        InMemoryUserRepository.save(user);
    }

    private void checkIsDuplicatedAccount(String account) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresent((user) -> new IllegalArgumentException("이미 존재하는 유저입니다."));
    }
}
