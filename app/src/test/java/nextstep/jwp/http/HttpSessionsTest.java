package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.http.entity.HttpSession;
import org.junit.jupiter.api.Test;

class HttpSessionsTest {
    @Test
    void getSession() {
        HttpSession actual = HttpSessions.getSession("testId");

        assertThat(actual).isEqualTo(new HttpSession("testId"));
    }
}
