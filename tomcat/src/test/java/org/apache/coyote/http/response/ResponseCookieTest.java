package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

class ResponseCookieTest {

    private final Manager manager = SessionManager.getInstance();

    @Test
    void ResponseCookie를_조립한다() {
        // given
        ResponseCookie responseCookie = new ResponseCookie();
        responseCookie.addSessionCookie(new Session("656cef62-e3c4-40bc-a8df-94732920ed46", manager));

        // when
        StringBuilder builder = new StringBuilder();
        responseCookie.assemble(builder);

        // then
        String expected = "Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 \r\n";
        assertThat(builder.toString()).isEqualTo(expected);
    }
}
