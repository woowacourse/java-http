package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import org.apache.coyote.http11.common.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("200 OK 응답을 생성할 수 있어야 한다")
    void testOkResponse() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = HttpResponse.ok(outputStream);

        response.write();

        String expectedResponse = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 0 ",
                "",
                ""
        );

        assertThat(outputStream).hasToString(expectedResponse);
    }

    @Test
    @DisplayName("리다이렉트 시 상태 코드와 Location 헤더를 생성할 수 있어야 한다")
    void testSendRedirect() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = HttpResponse.ok(outputStream);

        response.sendRedirect("/index.html");
        response.write();

        String expectedResponse = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                ""
        );

        assertThat(outputStream).hasToString(expectedResponse);
    }

    @Test
    @DisplayName("addCookie 시 Set-Cookie 헤더를 생성할 수 있어야 한다")
    void testAddCookie() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = HttpResponse.ok(outputStream);

        response.addCookie(Cookie.of("name", "value"));
        response.write();

        String expectedResponse = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Set-Cookie: name=value ",
                "Content-Length: 0 ",
                "",
                ""
        );

        assertThat(outputStream).hasToString(expectedResponse);
    }
}
