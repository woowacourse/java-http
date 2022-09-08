package org.apache.coyote.http11.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void getId() {
        // given
        String id = "my-id";
        // when
        Session session = new Session(id);
        // then
        assertThat(session.getId()).isEqualTo(id);
    }

    @Test
    void setAndGetAttribute() {
        // given
        Session session = new Session("my-id");
        session.setAttribute("hello", "world");
        // when
        Object attribute = session.getAttribute("hello");
        // then
        Assertions.assertAll(
                () -> assertThat(attribute).isInstanceOf(String.class),
                () -> assertThat((String) attribute).isEqualTo("world")
        );
    }
}
