package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.catalina.session.Session;
import org.junit.jupiter.api.Test;

public class SessionTest {

    @Test
    void null을_설정하면_기존_속성을_삭제() {
        Session session = new Session("session-id");
        session.setAttribute("user", "minjun");
        session.setAttribute("user", null);

        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void 존재하지_않는_속성에_null을_설정해도_예외가_발생하지_않는다() {
        Session session = new Session("session-id");
        assertThatCode(() -> session.setAttribute("user", null))
                .doesNotThrowAnyException();

        assertThat(session.getAttribute("user")).isNull();
    }
}
