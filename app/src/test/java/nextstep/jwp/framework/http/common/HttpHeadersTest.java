package nextstep.jwp.framework.http.common;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.framework.http.session.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("쿠키를 추가한다.")
    @Test
    void addCookie() {
        //given
        HttpHeaders headers = new HttpHeaders(
            "Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12 \r\nCookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1");

        //when
        headers.addCookie();

        //then
        assertThat(headers.hasCookie()).isFalse();
        //assertThat(headers.cookieToString()).isEqualTo("Set-Cookie: JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1; \r\n");
    }

    @DisplayName("세션에 추가한다.")
    @Test
    void addSession() {
        //given
        HttpHeaders headers = new HttpHeaders(
            "Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12 \r\nCookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1");

        //when
        headers.addCookie();
        HttpSession sessions = headers.sessions();

        //then
        assertThat(headers.hasCookie()).isFalse();
        assertThat(sessions.getId()).isEqualTo("acbd813f-eb5a-4f8d-87fe-b1737e0871a1");
    }
}
