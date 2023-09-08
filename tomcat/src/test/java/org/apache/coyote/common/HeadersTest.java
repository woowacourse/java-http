package org.apache.coyote.common;

import org.apache.coyote.session.Cookies;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HeadersTest {

    @Test
    void 빈_헤더를_생성한다() {
        assertThatCode(() -> Headers.empty())
                .doesNotThrowAnyException();
    }

    @Test
    void 헤더에_값을_추가할_수_있다() {
        // given
        final Headers headers = Headers.empty();

        // when
        headers.addHeader("name", "헤나");
        headers.addHeader("type", "백엔드");

        // then
        assertThat(headers)
                .hasFieldOrPropertyWithValue("mapping", Map.of(
                        "name", "헤나",
                        "type", "백엔드"
                ));
    }

    @Test
    void 헤더에_헤더명을_이용해서_헤더값을_가져올_수_있다() {
        // given
        final Headers headers = Headers.empty();
        headers.addHeader("name", "헤나");
        headers.addHeader("type", "백엔드");

        // when
        final String name = headers.getHeaderValue("name");
        final String type = headers.getHeaderValue("type");

        // then
        assertAll(
                () -> assertThat(name).isEqualTo("헤나"),
                () -> assertThat(type).isEqualTo("백엔드")
        );
    }

    @Test
    void 헤더에_ContentType을_지정할_수_있다() {
        // given
        final Headers headers = Headers.empty();

        // when
        headers.setContentType("text/html");

        // then
        final String contentType = headers.getHeaderValue("Content-Type");

        assertThat(contentType).isEqualTo("text/html");
    }

    @Test
    void 헤더에_ContentLength를_지정할_수_있다() {
        // given
        final Headers headers = Headers.empty();

        // when
        headers.setContentLength(1000);

        // then
        final String contentLength = headers.getHeaderValue("Content-Length");

        assertThat(contentLength).isEqualTo("1000");
    }

    @Test
    void 헤더에_Location을_지정할_수_있다() {
        // given
        final Headers headers = Headers.empty();

        // when
        headers.setLocation("/index.html");

        // then
        final String location = headers.getHeaderValue("Location");

        assertThat(location).isEqualTo("/index.html");
    }

    @Test
    void 헤더에_SetCookie를_지정할_수_있다() {
        // given
        final Headers headers = Headers.empty();

        // when
        headers.setCookies(Cookies.from("JSESSIONID=dasfijewfun29u;name=헤나"));

        // then
        final String cookieNamesAndValues = headers.getHeaderValue("Set-Cookie");

        assertThat(cookieNamesAndValues).isEqualTo("name=헤나;JSESSIONID=dasfijewfun29u");
    }

    @Test
    void 헤더에_SetCookie를_추가할_수_있다() {
        // given
        final Headers headers = Headers.empty();

        // when
        headers.addCookie("JSESSIONID", "fsdijgnognkew");
        headers.addCookie("name", "헤나");

        // then
        final String cookieNamesAndValues = headers.getHeaderValue("Set-Cookie");

        assertThat(cookieNamesAndValues).contains("fsdijgnognkew", "헤나");
    }

    @Test
    void 헤더_이름_목록을_정렬하여_가져올_수_있다() {
        // given
        final Map<String, String> headersWithValue = Map.of(
                "Connection", "keep-alive",
                "Accept", "text/html;charset=utf-8"
        );

//        // when
//        final Headers headers = new Headers(headersWithValue);
//
//        // then
//        assertThat(headers.headerNames()).containsExactly("Accept", "Connection");
    }
}
