package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RequestUriTest {

    private final String URL = "/index.html?key1=value1&key2=value2";

    @DisplayName("URL String을 전달받으면 객체가 생성된다.")
    @Test
    void constructor() {
        // given & when
        RequestUri actual = new RequestUri(URL);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("쿼리 스트링을 가져올 수 있다.")
    @Test
    void getQuery() {
        // given
        RequestUri requestUri = new RequestUri(URL);

        // when
        String queryString1 = requestUri.getQuery("key1").get();
        String queryString2 = requestUri.getQuery("key2").get();

        // then
        assertAll(() -> {
            assertThat(queryString1).isEqualTo("value1");
            assertThat(queryString2).isEqualTo("value2");
        });
    }

    @DisplayName("쿼리 스트링을 제외한 경로를 가져올 수 있다.")
    @ValueSource(strings = {"/index.html?key1=value1&key2=value2", "/index.html"})
    @ParameterizedTest
    void getPath(String url) {
        // given
        RequestUri requestUri = new RequestUri(url);

        // when
        String actual = requestUri.getPath();

        // then
        assertThat(actual).isEqualTo("/index.html");
    }

    @DisplayName("파일의 확장자를 가져올 수 있다.")
    @Test
    void getExtension() {
        // given
        RequestUri requestUri = new RequestUri(URL);

        // when
        String actual = requestUri.getExtension();

        // then
        assertThat(actual).isEqualTo("html");
    }

    @DisplayName("파일의 확장자가 없다면 빈 스트링을 반환한다.")
    @Test
    void getExtension_returnsOptionalEmpty() {
        // given
        String url = "/login?hello=world";
        RequestUri requestUri = new RequestUri(url);

        // when
        String actual = requestUri.getExtension();

        // then
        assertThat(actual).isEqualTo("");
    }

    @DisplayName("URI의 쿼리 존재 유무를 반환한다.")
    @CsvSource(value = {"/login?hello=world,true", "/login,false", "/login?,false"})
    @ParameterizedTest
    void hasQuery(final String uri, final boolean expected) {
        // given
        RequestUri requestUri = new RequestUri(uri);

        // when
        boolean actual = requestUri.hasQuery();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
