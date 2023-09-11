package common.http;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionTest {

    @Test
    void 세션의_속성을_가져온다() {
        // given
        Session session = new Session("id");
        session.setAttribute("user", "로이스");

        // when
        Object value = session.getAttribute("user");

        // then
        assertThat(value).isEqualTo("로이스");
    }

    @Test
    void 세션에서_속성을_삭제한다() {
        // given
        Session session = new Session("id");
        session.setAttribute("user", "로이스");
        session.setAttribute("reviewee", "리오");

        // when
        session.removeAttribute("user");

        // then
        assertThat(session.getAttribute("user")).isNull();
        assertThat(session.getAttribute("reviewee")).isEqualTo("리오");
    }

}
