package nextstep.jwp.service;

import nextstep.jwp.controller.request.RegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;

public class RegisterService {

    private final InMemoryUserRepository userRepository;

    public RegisterService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(RegisterRequest registerRequest) {
        userRepository.save(registerRequest.toEntity());
    }
}
