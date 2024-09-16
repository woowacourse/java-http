package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpMessageBodyInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    private HttpRequestHeader httpRequestHeader;

    @DisplayName("헤더에 Content-Length 키가 있으면 컨텐츠 길이를 반환한다.")
    @Test
    void getContentLengthExists() {
        Map<String, List<String>> values = new HashMap<>();
        values.put(HttpMessageBodyInfo.CONTENT_LENGTH.getValue(), List.of("10"));
        httpRequestHeader = new HttpRequestHeader(values);

        int contentLength = httpRequestHeader.getContentLength();

        assertThat(contentLength).isEqualTo(10);
    }

    @DisplayName("헤더에 Content-Length 키가 있으면 0을 반환한다.")
    @Test
    void getContentLengthNotExists() {
        httpRequestHeader = new HttpRequestHeader(Map.of());

        int contentLength = httpRequestHeader.getContentLength();

        assertThat(contentLength).isEqualTo(0);
    }

    @DisplayName("쿠키가 존재하면 true를 반환한다")
    @Test
    void hasCookie() {
        Map<String, List<String>> values = new HashMap<>();
        values.put(HttpMessageBodyInfo.COOKIE.getValue(), List.of("name=kaki"));
        httpRequestHeader = new HttpRequestHeader(values);

        boolean exists = httpRequestHeader.hasCookie();

        assertThat(exists).isTrue();
    }

    @DisplayName("쿠키가 존재하면 쿠키를 반환한다.")
    @Test
    void getCookieExists() {
        Map<String, List<String>> values = new HashMap<>();
        values.put(HttpMessageBodyInfo.COOKIE.getValue(), List.of("name=kaki"));
        httpRequestHeader = new HttpRequestHeader(values);

        assertThat(httpRequestHeader.getCookie()).isInstanceOf(Cookie.class);
    }

    @DisplayName("쿠키가 존재하지 않으면 null을 반환한다.")
    @Test
    void getCookieNotExists() {
        httpRequestHeader = new HttpRequestHeader(Map.of());

        assertThat(httpRequestHeader.getCookie()).isNull();
    }

    @DisplayName("세션이 존재하면 true를 반환한다.")
    @Test
    void hasSession() {
        Map<String, List<String>> values = new HashMap<>();
        values.put(HttpMessageBodyInfo.COOKIE.getValue(), List.of("JSESSIONID=abc123"));
        httpRequestHeader = new HttpRequestHeader(values);

        boolean exists = httpRequestHeader.hasSession();

        assertThat(exists).isTrue();
    }

    @DisplayName("세션이 존재하지 않으면 false를 반환한다.")
    @Test
    void hasNotSession() {
        Map<String, List<String>> values = new HashMap<>();
        values.put(HttpMessageBodyInfo.COOKIE.getValue(), List.of("name=kaki"));
        httpRequestHeader = new HttpRequestHeader(values);

        boolean exists = httpRequestHeader.hasSession();

        assertThat(exists).isFalse();
    }
}
