package nextstep.org.apache.catalina.servlet.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.apache.catalina.servlet.session.InvalidateSessionException;
import org.apache.catalina.servlet.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("Session 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class SessionTest {

    @Test
    void 속성을_추가하고_조회할_수_있다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());

        // when
        session.setAttribute("mallang", "1234");

        // then
        assertThat(session.getAttribute("mallang")).isEqualTo("1234");
    }

    @Test
    void 속성을_제거할_수_있다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("mallang", "1234");

        // when
        session.removeAttribute("mallang");

        // then
        assertThat(session.getAttribute("mallang")).isNull();
    }

    @Test
    void 만료시킬_수_있다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());

        // when
        session.invalidate();

        // then
        assertThat(session.isInvalidate()).isTrue();
    }

    @Test
    void 만료된_상태에서는_속성을_추가하거나_조회_제거할_수_없다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        session.invalidate();

        // when & then
        assertThatThrownBy(() -> {
                    session.setAttribute("na", "123");
                }
        ).isInstanceOf(InvalidateSessionException.class);
    }
}
