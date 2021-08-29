package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LoginTest {

    @Test
    void isSuccess() {
        //given
        final String id = "gugu";
        final Map<String, String> map = new HashMap<>();
        map.put("account", id);
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