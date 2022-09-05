package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.apache.coyote.web.session.Cookie;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    void setContentLength() {
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());
        httpHeaders.setContentLength(10);

        assertThat(httpHeaders.getContentLength()).isEqualTo("10");
    }

    @Test
    void setContentType() {
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());
        httpHeaders.setContentType(ContentType.TEXT_HTML_CHARSET_UTF_8);

        assertThat(httpHeaders.getContentType()).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8.getValue());
    }

    @Test
    void setCookie() {
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());
        httpHeaders.setCookie(new Cookie("JSESSIONID", "session"));

        assertThat(httpHeaders.getHeader(HttpHeader.SET_COOKIE.getValue()).get()).isEqualTo("JSESSIONID=session");
    }

    @Test
    void addCookie() {
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());
        httpHeaders.setCookie(new Cookie("JSESSIONID", "session"));

        httpHeaders.addCookie(new Cookie("My", "Cookie"));
        assertThat(httpHeaders.getHeader(HttpHeader.SET_COOKIE.getValue()).get()).isEqualTo(
                "JSESSIONID=session; My=Cookie");
    }
}
