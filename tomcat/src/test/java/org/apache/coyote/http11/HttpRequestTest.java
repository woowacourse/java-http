package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void parsesHeadersCookiesAndFormParameters() throws Exception {
        final String body = "account=new%2Duser&password=password";
        final String rawRequest = String.join("\r\n",
                "POST /register?source=home HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Cookie: JSESSIONID=session-id",
                "",
                body);

        final HttpRequest request = new HttpRequest(
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));

        assertThat(request.getPath()).isEqualTo("/register");
        assertThat(request.getParameter("source")).isEqualTo("home");
        assertThat(request.getParameter("account")).isEqualTo("new-user");
        assertThat(request.getHeader("content-type")).isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getCookie("JSESSIONID")).isEqualTo("session-id");
    }
}
