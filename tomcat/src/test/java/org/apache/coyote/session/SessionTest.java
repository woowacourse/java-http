package org.apache.coyote.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    @DisplayName("세션에 정보를 저장하고 조회할 수 있다.")
    void sessionAddTest () {
        Session session = new Session("id");

        session.setAttribute("key", "value");

        assertThat(session.getAttribute("key"))
                .isEqualTo("value");
    }

    @Test
    @DisplayName("세션에 정보를 삭제하면 정보 조회시 null 이 반환된다.")
    void sessionAttributeRemoveTest () {
        Session session = new Session("id");

        session.setAttribute("key", "value");
        session.removeAttribute("key");

        assertThat(session.getAttribute("key"))
                .isNull();
    }

    @Test
    @DisplayName("세션을 만료 시키면 모든 데이터가 삭제된다..")
    void sessionInvalidateTest () {
        Session session = new Session("id");

        session.setAttribute("key1", "value1");
        session.setAttribute("key2", "value2");
        session.setAttribute("key3", "value3");
        session.invalidate();

        assertAll(
                () -> assertThat(session.getAttribute("key1"))
                        .isNull(),
                () -> assertThat(session.getAttribute("key2"))
                        .isNull(),
                () -> assertThat(session.getAttribute("key3"))
                        .isNull()
        );
    }
}
