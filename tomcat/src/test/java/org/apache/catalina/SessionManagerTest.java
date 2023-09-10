package org.apache.catalina;

import java.util.Objects;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionManagerTest {
    @Test
    void throwExceptionWhenKeyisNull() {
        //when
        //then
        assertThatThrownBy(() -> SessionManager.findSession(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void returnNullWhen() {
        //given

        //when
        final var session = SessionManager.findSession("not exist id");
        //then
        assertThat(session).isEmpty();
    }
}
