package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    private final Manager manager = SessionManager.getInstance();

    @Test
    void ResponseHeaders를_조립한다() {
        // given
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.setContentType("text/html");
        responseHeaders.setContentLength(1024);

        // when
        StringBuilder builder = new StringBuilder();
        responseHeaders.assemble(builder);

        // then
        String expected = """
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 1024 \r
                \r
                """;
        assertThat(builder.toString()).isEqualTo(expected);
    }

    @Test
    void 쿠키가_있다면_Set_Cookie_헤더도_함께_조립한다() {
        // given
        Session session = manager.createSession("656cef62-e3c4-40bc-a8df-94732920ed46");
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.setContentType("text/html");
        responseHeaders.setContentLength(1024);
        responseHeaders.addSessionCookie(session);

        // when
        StringBuilder builder = new StringBuilder();
        responseHeaders.assemble(builder);

        // then
        String expected = """
                Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 \r
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 1024 \r
                \r
                """;
        assertThat(builder.toString()).isEqualTo(expected);
    }
}
