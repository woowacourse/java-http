package nextstep.jwp.web;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {
    @Test
    @DisplayName("세션의 속성값을 세팅하고 얻어올 수 있다.")
    void sessionAttribute() {
        // given
        String jSessionId = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(jSessionId);

        // when
        User user = new User("gugu", "password", "hkkang@woowahan.com");
        httpSession.setAttribute("user", user);

        // then
        User expected = new User("gugu", "password", "hkkang@woowahan.com");
        assertThat(httpSession.getAttribute("user"))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

    }
}
