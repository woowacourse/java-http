package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.Http11Processor.Session;
import org.apache.coyote.http11.Http11Processor.SessionManager;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 세션에_저장한_속성을_조회하고_삭제한다() {
        String id = UUID.randomUUID().toString();
        Session session = new Session(id);
        session.setAttribute("user", "gugu");

        assertThat(session.getId()).isEqualTo(id);
        assertThat(session.getAttribute("user")).isEqualTo("gugu");

        session.removeAttribute("user");

        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void Manager로_세션을_등록하고_조회하고_삭제한다() throws IOException {
        Manager manager = SessionManager.getInstance();
        Session session = new Session(UUID.randomUUID().toString());
        manager.add(session);

        assertThat(manager.findSession(session.getId())).isSameAs(session);

        manager.remove(session);

        assertThat(manager.findSession(session.getId())).isNull();
    }

    @Test
    void 세션을_무효화하면_저장소에서_제거되고_사용할_수_없다() {
        SessionManager manager = SessionManager.getInstance();
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", "gugu");
        manager.add(session);

        session.invalidate();

        assertThat(manager.findSession(session.getId())).isNull();
        assertThatThrownBy(() -> session.getAttribute("user"))
                .isInstanceOf(IllegalStateException.class);
    }
}
