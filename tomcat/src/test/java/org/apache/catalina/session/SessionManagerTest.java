package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    @DisplayName("세션 아이디로 세션을 조회한다.")
    void findSession() {
        // given
        SessionManager manager = SessionManager.getInstance();
        User user = new User("seyang", "pw", "se@yang.com");
        HttpSession expected = manager.createSession(user);
        String sessionId = expected.getId();

        // when
        HttpSession actual = manager.findSession(sessionId);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("HTTP 요청으로 세션을 조회한다.")
    void getSession() {
        // given
        SessionManager manager = SessionManager.getInstance();
        User user = new User("seyang", "pw", "se@yang.com");
        HttpSession expected = manager.createSession(user);
        HttpRequest request = HttpRequest.parse(List.of(
                "POST /login HTTP/1.1",
                "Cookie: " + JSession.COOKIE_NAME + "=" + expected.getId()
        ));

        // when
        HttpSession actual = manager.getSession(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
