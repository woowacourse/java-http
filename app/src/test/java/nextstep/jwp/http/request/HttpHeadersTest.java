package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("Request header 에서 쿠키를 가져오기 성공")
    @Test
    void getCookie() {
        final HttpCookie expected = new HttpCookie("TEST=1234");
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), expected);

        HttpCookie actual = httpHeaders.getCookie();
        assertThat(actual.getCookies("TEST")).isEqualTo(expected.getCookies("TEST"));
    }

    @DisplayName("request header 에서 쿠키 가져오기 실패 - 쿠키에 해당 값이 없음")
    @Test
    void getCookie_null() {
        final HttpCookie expected = new HttpCookie("TEST=1234");
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), expected);

        HttpCookie actual = httpHeaders.getCookie();
        assertThat(actual.getCookies("WRONG-TEST")).isNull();
    }

    @DisplayName("request 가 body 를 가지고 있는지 성공")
    @Test
    void hasBody_true() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "4");
        headers.put("Content-Body", "test");
        HttpHeaders httpHeaders = new HttpHeaders(headers, new HttpCookie());

        assertThat(httpHeaders.hasBody()).isTrue();
    }

    @DisplayName("request 가 body 를 가지고 있는지 - Content-Length, Content-Body 없음")
    @Test
    void hasBody_false() {
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), new HttpCookie());

        assertThat(httpHeaders.hasBody()).isFalse();
    }

    @DisplayName("특정 키로 헤더 가져오기 성공")
    @Test
    void getHeaderBy() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "4");
        headers.put("Content-Body", "test");
        HttpHeaders httpHeaders = new HttpHeaders(headers, new HttpCookie());

        assertThat(httpHeaders.getHeaderBy("Content-Length")).isEqualTo("4");
        assertThat(httpHeaders.getHeaderBy("Content-Body")).isEqualTo("test");
    }

    @DisplayName("특정 키로 헤더 가져오기 실패 - 해당하는 키의 헤더가 없음")
    @Test
    void getHeaderBy_fail() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("test", "test");
        HttpHeaders httpHeaders = new HttpHeaders(headers, new HttpCookie());

        assertThat(httpHeaders.getHeaderBy("wrong-header")).isNull();
    }
}