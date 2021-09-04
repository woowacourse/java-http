package nextstep.jwp.service;

import nextstep.jwp.controller.request.RegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterService.class);

    private final InMemoryUserRepository userRepository;

    public RegisterService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(RegisterRequest registerRequest) {
        User user = registerRequest.toEntity();
        userRepository.save(user);

        LOGGER.debug("{} Account User Successful Register.", user.getAccount());
    }
}
