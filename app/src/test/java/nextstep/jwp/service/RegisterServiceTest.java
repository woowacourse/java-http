package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterServiceTest {

    @DisplayName("회원가입 테스트")
    @Test
    void register() {
        RequestBody requestBody = new RequestBody("account=02&password=1234&email=abc@abc.com");

        RegisterService registerService = new RegisterService();
        registerService.register(requestBody);

        User user = InMemoryUserRepository.findByAccount("02")
            .orElseThrow();

        assertThat(user.getAccount()).isEqualTo("02");
    }

}
