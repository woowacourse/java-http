package org.apache.coyote.http11;

import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void request_start_line을_파싱한다() throws IOException {
        String rawRequest = "GET /login?account=jjangu&password=hooni HTTP/1.1";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawRequest.getBytes());
        HttpRequest httpRequest = new HttpRequest(byteArrayInputStream, new SessionManager());

        String path = httpRequest.getPath();
        String accountValue = httpRequest.getParameter("account");
        String passwordValue = httpRequest.getParameter("password");

        assertThat(path).isEqualTo("/login");
        assertThat(accountValue).isEqualTo("jjangu");
        assertThat(passwordValue).isEqualTo("hooni");
    }

}