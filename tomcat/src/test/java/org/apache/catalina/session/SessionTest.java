package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void getAttributeTest_whenAttributeNotExist() {
        Session session = new Session("id");

        Object actual = session.getAttribute("not-exist");

        assertThat(actual).isNull();
    }

    @Test
    void setAttributeTest() {
        Session session = new Session("id");
        Object value = new Object();

        session.setAttribute("name", value);

        assertThat(session.getAttribute("name"))
                .isExactlyInstanceOf(Object.class)
                .isSameAs(value);
    }

    @Test
    void removeAttributeTest() {
        Session session = new Session("id");
        Object value = new Object();
        session.setAttribute("name", value);

        session.removeAttribute("name");

        assertThat(session.getAttribute("name"))
                .isNull();
    }

    @Test
    void invalidateTest() {
        Session session = new Session("id");
        session.setAttribute("name", "value");
        session.setAttribute("name2", "value2");

        session.invalidate();

        assertAll(
                () ->  assertThat(session.getAttribute("name")).isNull(),
                () ->  assertThat(session.getAttribute("name2")).isNull()
        );
    }
}
