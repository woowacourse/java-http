package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpHeadersTest {

    @Test
    void 문자열_리스트를_받아_HttpHeaders를_만든다() {
        // given
        List<String> strings = List.of(
                "Content-Length: 12",
                "Content-Type: text/css");

        // when
        HttpHeaders headers = HttpHeaders.create(strings);

        // then
        assertThat(headers.getHeader("Content-Length")).isEqualTo("12");
        assertThat(headers.getHeader("Content-Type")).isEqualTo("text/css");
    }

    @Test
    void header를_추가할_수_있다() {
        // given
        List<String> strings = List.of(
                "Content-Length: 12",
                "Content-Type: text/css");
        HttpHeaders headers = HttpHeaders.create(strings);

        // when
        headers.addHeader(HttpHeaderName.LOCATION, "/index.html");

        // then
        assertThat(headers.getHeader("Content-Length")).isEqualTo("12");
        assertThat(headers.getHeader("Content-Type")).isEqualTo("text/css");
        assertThat(headers.getHeader("Location")).isEqualTo("/index.html");
    }

    @Test
    void headers에서_cookie를_가져올_수_있다() {
        // given
        List<String> strings = List.of(
                "Cookie: oing=hello; gugu=hi",
                "Content-Type: text/css");
        HttpHeaders headers = HttpHeaders.create(strings);

        // when
        Cookies cookies = headers.getCookies();

        // then
        assertThat(cookies.getCookie("oing")).isEqualTo("hello");
        assertThat(cookies.getCookie("gugu")).isEqualTo("hi");
    }
}
