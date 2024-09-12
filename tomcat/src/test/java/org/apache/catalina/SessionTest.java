package org.apache.catalina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @DisplayName("랜덤 세션 생성 성공")
    @Test
    void createRandomSession() {
        //given & when
        Session session = Session.createRandomSession();

        // then
        assertThat(UUID.fromString(session.getId())).isInstanceOf(UUID.class);
    }

    @DisplayName("attribute 추가 성공")
    @Test
    void setAttribute() {
        // given
        String id = "id";
        Session session = new Session(id);

       // when
        String name = "name";
        String value = "value";
        session.setAttribute(name, value);

        // then
        assertThat(session.getAttribute(name)).isEqualTo(value);
    }

    @DisplayName("attribute 삭제 성공")
    @Test
    void removeAttribute() {
        // given
        String id = "id";
        Session session = new Session(id);

        String name = "name";
        String value = "value";
        session.setAttribute(name, value);

        // when
        session.removeAttribute(name);


        // then
        assertThat(session.getAttribute(name)).isNull();
    }
}
