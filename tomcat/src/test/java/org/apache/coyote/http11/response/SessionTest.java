package org.apache.coyote.http11.response;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class SessionTest {

    @Test
    void 세션_생성_테스트() {
        // given
        String id = "1234";

        // when
        Session session = new Session(id);

        // then
        assertThat(session.getId()).isEqualTo(id);
    }

    @Test
    void 세션_이름_저장_테스트() {
        // given
        String id = "1234";
        String name = "name";
        String value = "kiara";

        // when
        Session session = new Session(id);
        session.setAttribute(name, value);

        // then
        assertThat(session.getAttribute(name)).isEqualTo(value);
    }

    @Test
    void 세션_사용자_저장_테스트() {
        // given
        String id = "1234";
        User user = new User("kiara", "kiara@gmail.com", "1234");

        // when
        Session session = new Session(id);
        session.setAttribute("user", user);

        // then
        assertThat(session.getAttribute("user")).isEqualTo(user);
    }
}
