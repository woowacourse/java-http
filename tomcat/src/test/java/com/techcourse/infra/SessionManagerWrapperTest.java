package com.techcourse.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Http11Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerWrapperTest {

    @Test
    @DisplayName("세션을 아이디로 조회할 수 있다.")
    void findById() {
        SessionManager manager = new SessionManager();
        SessionManagerWrapper sessionManagerWrapper = new SessionManagerWrapper(manager);
        Session session = new Session("robin");
        sessionManagerWrapper.add(session);

        assertAll(
                () -> assertThat(sessionManagerWrapper.findById("other")).isEmpty(),
                () -> assertThat(sessionManagerWrapper.findById("robin")).hasValue(session)
        );
    }

    @Test
    @DisplayName("세션을 쿠키로 조회할 수 있다.")
    void findBySessionCookie() {
        SessionManager manager = new SessionManager();
        SessionManagerWrapper sessionManagerWrapper = new SessionManagerWrapper(manager);
        Session session = new Session("robin");
        sessionManagerWrapper.add(session);

        Http11Cookie sessionCookie = new Http11Cookie("JSESSIONID", "robin");
        assertThat(sessionManagerWrapper.findBySessionCookie(sessionCookie)).hasValue(session);
    }
}
