package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 세션에_정보를_저장하고_조회할_수_있다() {
        // given
        Session session = new Session("jsessionid");

        // when
        String value = "seong-wooo";
        session.setAttribute("name", value);

        // then
        assertThat(session.getAttribute("name")).isEqualTo(value);
    }

    @Test
    void 세션에_이미_저장된_key값을_새로_저장하면_value가_업데이트된다() {
        // given
        Session session = new Session("jsessionid");

        // when
        String value = "seong-wooo";
        session.setAttribute("name", value);

        // then
        session.setAttribute("name", Map.of());
        assertThat(session.getAttribute("name")).isEqualTo(Map.of());
    }

    @Test
    void 세션에_저장되지않은_key값을_가져오면_null이_반환된다() {
        // given
        Session session = new Session("jsessionid");

        // then
        assertThat(session.getAttribute("name")).isEqualTo(null);
    }
}
