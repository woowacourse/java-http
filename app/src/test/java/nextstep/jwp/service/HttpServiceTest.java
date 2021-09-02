package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.constants.UserParams;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpServiceTest {
    @Test
    @DisplayName("중복된 account 등록시 예외가 발생한다.")
    void register() {
        User user = new User(1L, "gugu", "password", "email@email.com");
        MockInMemoryRepository.save(user);
        Map<String, String> params = new HashMap<>();
        params.put(UserParams.ACCOUNT, "gugu");

        assertThatThrownBy(() -> HttpService.register(params)).isInstanceOf(BadRequestException.class);
    }
}