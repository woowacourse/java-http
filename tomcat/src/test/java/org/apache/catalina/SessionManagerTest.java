package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("세션이 잘 생성되는지 검증한다.")
    @Test
    void checkCreateSession() {
        final String jsessionid = HttpCookie.makeJSESSIONID();
        final Session session = SessionManager.add(jsessionid);

        assertThat(SessionManager.findSession(jsessionid)).isEqualTo(session);
    }
}
