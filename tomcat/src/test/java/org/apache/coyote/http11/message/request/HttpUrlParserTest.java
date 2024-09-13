package org.apache.coyote.http11.message.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpUrlParserTest {

    @Test
    @DisplayName("쿼리스트링이 없는 URL을 파싱한다.")
    void parseUrlWithoutQueryStringTest() {
        // given
        String url = "/index.html";

        // when
        HttpUrl httpUrl = HttpUrlParser.parseUrl(url);

        // then
        assertAll(
                () -> assertEquals("/index.html", httpUrl.getPath()),
                () -> assertFalse(httpUrl.hasQueryString())
        );
    }

    @Test
    @DisplayName("쿼리스트링이 있는 URL을 파싱한다.")
    void parseUrlWithQueryStringTest() {
        // given
        String url = "/index.html?key1=value1&key2=value2";

        // when
        HttpUrl httpUrl = HttpUrlParser.parseUrl(url);

        // then
        assertAll(
                () -> assertEquals("/index.html", httpUrl.getPath()),
                () -> assertEquals("value1", httpUrl.getQueryParameter("key1")),
                () -> assertEquals("value2", httpUrl.getQueryParameter("key2"))
        );
    }
}
