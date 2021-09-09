package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.entity.HttpSession;
import org.junit.jupiter.api.Test;

class HttpSessionsTest {
    @Test
    void getSessionWithEmptySessionRequest() {
        HttpSession actual = HttpSessions.getSession("NotContainedKey");

        assertThat(actual).isNotNull();
    }
}
