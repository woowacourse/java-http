package nextstep.jwp.framework.http.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpSessionsTest {

    @DisplayName("세션에 등록한다.")
    @Test
    void add() {
        //given
        HttpSessions.putSession(new HttpSession("12345"));

        //when
        HttpSession session = HttpSessions.getSession("12345");

        //then
        assertThat(session.getId()).isEqualTo("12345");
    }
}
