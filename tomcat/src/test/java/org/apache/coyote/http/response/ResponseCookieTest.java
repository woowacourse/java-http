package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.FixedIdGenerator;

class ResponseCookieTest {

    @Test
    void ResponseCookie를_조립한다() {
        // given
        Manager manager = SessionManager.getInstance();
        manager.setIdGenerator(new FixedIdGenerator());
        Session session = manager.createSession();

        ResponseCookie responseCookie = new ResponseCookie();
        responseCookie.addSessionCookie(session);

        // when
        StringBuilder builder = new StringBuilder();
        responseCookie.assemble(builder);

        // then
        String expected = "Set-Cookie: JSESSIONID=fixed-id \r\n";
        assertThat(builder.toString()).isEqualTo(expected);
    }
}
