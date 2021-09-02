package nextstep.jwp.db;

import nextstep.jwp.web.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpSessionsTest {
    @Test
    @DisplayName("새로운 세션을 발급하고, 저장소에 세션을 추가한다.")
    void issueHttpSession() {
        // given, when
        HttpSession expected = HttpSessions.issueSession();
        HttpSession actual = HttpSessions.getSession(expected.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
