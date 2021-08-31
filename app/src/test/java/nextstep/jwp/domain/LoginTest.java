package nextstep.jwp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.domain.Login;
import org.junit.jupiter.api.Test;

class LoginTest {

    @Test
    void isSuccess() {
        //given
        final String id = "gugu";
        final String password = "password";
        final Map<String, String> map = new HashMap<>();
        map.put("account", id);
        map.put("password", password);
        final Login login = new Login(map);

        //when
        final boolean isSuccess = login.isSuccess();

        //then
        assertThat(isSuccess).isTrue();
    }

    @Test
    void isFailed() {
        final String id = "pigeon";
        final Map<String, String> map = new HashMap<>();
        map.put("account", id);
        final Login login = new Login(map);

        //when
        final boolean isSuccess = login.isSuccess();

        //then
        assertThat(isSuccess).isFalse();
    }
}