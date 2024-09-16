package org.apache.coyote.http11.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionTest {

    @Test
    @DisplayName("세션을 생성할 때 id도 함께 생성한다.")
    void should_create_session_id_when_session_created() {
        // given & when
        Session session = new Session();

        //then
        assertThat(session.getId()).isNotNull();
    }

    @Test
    @DisplayName("세션 생성 시 id는 randomUUID로 생성된다.")
    void should_session_id_create_to_randomUUID_when_session_created() {
        //given & when
        Session session1 = new Session();
        Session session2 = new Session();

        //then
        assertThat(session1.getId()).isNotEqualTo(session2.getId());
    }
}
