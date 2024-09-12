package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Nested
    class 세션 {

        @Test
        void 세션이_성공적으로_등록되고_확인된다() {
            // given
            SessionManager sessionManager = SessionManager.getInstance();
            String id = "ABCD";
            Session session = new Session(id);
            sessionManager.add(session);

            // when
            Session actual = sessionManager.findSession(id);

            // then
            assertThat(actual).isEqualTo(session);
        }

        @Test
        void 존재하지_않는_세션을_확인할_수_없다() {
            // given
            SessionManager sessionManager = SessionManager.getInstance();
            String id = "ABCD";
            Session session = new Session(id);
            sessionManager.add(session);

            // when
            Session actual = sessionManager.findSession("NotExist");

            // then
            assertThat(actual).isNull();
        }
    }
}
