package nextstep.jwp.service;

import nextstep.jwp.controller.request.RegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.model.User;

public class RegisterService {

    private final InMemoryUserRepository userRepository;

    public RegisterService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(RegisterRequest registerRequest) {
        User user = registerRequest.toEntity();
        validateDuplicateAccount(user);

        userRepository.save(user);
    }

    private void validateDuplicateAccount(User user) {
        if (userRepository.findByAccount(user.getAccount()).isPresent()) {
            throw new DuplicateAccountException();
        }
    }
}
