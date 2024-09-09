package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomHttpServletRequestTest {

    @Test
    @DisplayName("쿠키 값 가져온다.")
    void get_cookies() {
        // given
        final var httpRequest = "GET /example/path HTTP/1.1\r\n"
                + "Host: www.example.com\r\n"
                + "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9\r\n"
                + "Accept-Language: en-US,en;q=0.5\r\n"
                + "Accept-Encoding: gzip, deflate, br\r\n"
                + "Connection: keep-alive\r\n"
                + "Cookie: sessionId=abc123; theme=dark; username=johndoe\r\n"
                + "Upgrade-Insecure-Requests: 1";
        final var servletRequest = new CustomHttpServletRequest(httpRequest);

        // when
        final var cookies = servletRequest.getCookies();

        // then
        assertThat(cookies.length).isEqualTo(3);
    }

}
