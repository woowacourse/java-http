package org.apache.coyote.http11.request;

import org.apache.coyote.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class Http11RequestParserTest {

    @Test
    @DisplayName("요청 라인, 헤더, 본문을 파싱해 HttpRequest를 생성한다.")
    void parseHttpRequest() throws Exception {
        // given
        String body = "account=gugu&password=password";
        String message = String.join("\r\n",
                "POST /login?redirect=index HTTP/1.1",
                "host: localhost:8080",
                "content-length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "cookie: JSESSIONID=session-id",
                "",
                body);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = new Http11RequestParser().parse(inputStream);

        // then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getResourcePath()).isEqualTo("/login.html");
        assertThat(request.getParameter("redirect")).contains("index");
        assertThat(request.getParameter("account")).contains("gugu");
        assertThat(request.getCookie(Cookie.JSESSIONID))
                .map(Cookie::getValue)
                .contains("session-id");
    }

    @Test
    @DisplayName("요청 본문은 Content-Length 바이트만큼만 읽는다.")
    void readBodyByContentLengthBytes() throws Exception {
        // given
        String body = "account=러로";
        String message = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body
        ) + "TAIL";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = new Http11RequestParser().parse(inputStream);

        // then
        assertThat(request.getParameter("account")).contains("러로");
    }
}
