package nextstep.org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("SessionManager 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class SessionManagerTest {

    @Test
    void 세션을_추가할_수_있다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());

        // when
        SessionManager.add(session);

        // then
        Session found = SessionManager.findSession(session.id());
        assertThat(found).isEqualTo(session);
    }

    @Test
    void 세션을_제거할_수_있다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);

        // when
        SessionManager.remove(session.id());

        // then
        Session found = SessionManager.findSession(session.id());
        assertThat(found).isNull();
        assertThat(session.isInvalidate()).isTrue();
    }
}
