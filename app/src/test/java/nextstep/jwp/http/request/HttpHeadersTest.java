package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpCookie;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    void getCookie() {
        final HttpCookie expected = new HttpCookie("TEST=1234");
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), expected);

        HttpCookie actual = httpHeaders.getCookie();
        assertThat(actual.getCookies("TEST")).isEqualTo(expected.getCookies("TEST"));
    }

    @Test
    void getCookie_null() {
        final HttpCookie expected = new HttpCookie("TEST=1234");
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), expected);

        HttpCookie actual = httpHeaders.getCookie();
        assertThat(actual.getCookies("WRONG-TEST")).isNull();
    }

    @Test
    void hasBody_true() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "4");
        headers.put("Content-Body", "test");
        HttpHeaders httpHeaders = new HttpHeaders(headers, new HttpCookie());

        assertThat(httpHeaders.hasBody()).isTrue();
    }

    @Test
    void hasBody_false() {
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>(), new HttpCookie());

        assertThat(httpHeaders.hasBody()).isFalse();
    }

    @Test
    void getHeaderBy() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "4");
        headers.put("Content-Body", "test");
        HttpHeaders httpHeaders = new HttpHeaders(headers, new HttpCookie());

        assertThat(httpHeaders.getHeaderBy("Content-Length")).isEqualTo("4");
        assertThat(httpHeaders.getHeaderBy("Content-Body")).isEqualTo("test");
    }

    @Test
    void getHeaderBy_fail() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("test", "test");
        HttpHeaders httpHeaders = new HttpHeaders(headers, new HttpCookie());

        assertThat(httpHeaders.getHeaderBy("wrong-header")).isNull();
    }
}