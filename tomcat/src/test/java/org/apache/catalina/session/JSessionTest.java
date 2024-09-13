package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JSessionTest {

    @Test
    @DisplayName("세션 속성에 유저 객체를 넣어두고 읽는다.")
    void setAndGetAttributes() {
        // given
        JSession session = new JSession(UUID.randomUUID().toString());
        User user = new User("seyang", "pw", "se@yang.com");

        // when
        session.setAttribute("user", user);
        Object actual = session.getAttribute("user");

        // then
        assertThat(actual).isSameAs(user);
    }
}
