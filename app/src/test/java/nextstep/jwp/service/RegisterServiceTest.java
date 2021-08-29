package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.request.RegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterServiceTest {

    private RegisterService registerService;
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        Map<String, User> database = new HashMap<>();
        userRepository = new InMemoryUserRepository(database, 1L);

        registerService = new RegisterService(userRepository);
    }

    @DisplayName("register 시도시 이미 존재하는 account가 기입되면 예외가 발생한다.")
    @Test
    void registerException() {
        // given
        String account = "account";
        userRepository.save(new User(account, "pw", "em"));

        RegisterRequest request = new RegisterRequest(account, "password", "email");

        // when, then
        assertThatThrownBy(() -> registerService.registerUser(request))
            .isExactlyInstanceOf(DuplicateAccountException.class);
    }

}