package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.apache.catalina.Session;
import org.apache.coyote.http11.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11ResponseTest {

    @DisplayName("상태 코드 200인 응답을 생성한다.")
    @Test
    void ok() {
        Http11Response response = new Http11Response();
        String body = "hihi";

        response.ok(MimeType.HTML, body, StandardCharsets.UTF_8);
        String actual = response.getResponseMessage();
        System.out.println(actual);
        String expected = "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: " + body.getBytes().length + " \r\n"
                + "\r\n"
                + body;

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("상태 코드 302인 응답을 생성한다.")
    @Test
    void found() {
        Http11Response response = new Http11Response();
        String redirectPath = "/redirect";

        response.found(redirectPath);
        String actual = response.getResponseMessage();
        String expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /redirect\r\n"
                + "\r\n";

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("상태코드 404인 응답을 생성한다.")
    @Test
    void notFound() {
        Http11Response response = new Http11Response();
        String body = "NOT FOUND!!";

        response.notFound(body, StandardCharsets.UTF_8);
        String actual = response.getResponseMessage();
        String expected = "HTTP/1.1 404 Not Found \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: " + body.getBytes().length + " \r\n"
                + "\r\n"
                + body;

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Cookie에 세션ID를 담는다.")
    @Test
    void setSession() {
        Http11Response response = new Http11Response();
        Session session = new Session();
        response.setSession(session);

        String actual = response.getResponseMessage();
        String expected = "Set-Cookie: JSESSIONID=" + session.getId();
        assertThat(actual).contains(expected);
    }
}
