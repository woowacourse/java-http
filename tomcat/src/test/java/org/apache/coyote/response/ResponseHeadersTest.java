package org.apache.coyote.response;

import org.apache.coyote.common.Headers;
import org.apache.coyote.session.Cookies;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseHeadersTest {

    @Test
    void 헤더에_값을_추가할_수_있다() {
        // given
        final ResponseHeaders actual = ResponseHeaders.empty();

        // when
        actual.addHeader("name", "헤나");
        actual.addHeader("type", "백엔드");

        // then
        final Headers expected = Headers.empty();
        expected.addHeader("name", "헤나");
        expected.addHeader("type", "백엔드");

        assertAll(
                () -> assertThat(actual.headerNames()).containsExactlyInAnyOrder("name", "type"),
                () -> assertThat(actual.getHeaderValue("name")).isEqualTo("헤나"),
                () -> assertThat(actual.getHeaderValue("type")).isEqualTo("백엔드")
        );
    }

    @Test
    void 헤더에_헤더명을_이용해서_헤더값을_가져올_수_있다() {
        // given
        final ResponseHeaders responseHeaders = ResponseHeaders.empty();
        responseHeaders.addHeader("name", "헤나");
        responseHeaders.addHeader("type", "백엔드");

        // when
        final String name = responseHeaders.getHeaderValue("name");
        final String type = responseHeaders.getHeaderValue("type");

        // then
        assertAll(
                () -> assertThat(name).isEqualTo("헤나"),
                () -> assertThat(type).isEqualTo("백엔드")
        );
    }

    @Test
    void 헤더에_ContentType을_지정할_수_있다() {
        // given
        final ResponseHeaders responseHeaders = ResponseHeaders.empty();

        // when
        responseHeaders.setContentType("text/html");

        // then
        final String contentType = responseHeaders.getHeaderValue("Content-Type");

        assertThat(contentType).contains("text/html");
    }

    @Test
    void 헤더에_ContentLength를_지정할_수_있다() {
        // given
        final ResponseHeaders responseHeaders = ResponseHeaders.empty();

        // when
        responseHeaders.setContentLength(1000);

        // then
        final String contentLength = responseHeaders.getHeaderValue("Content-Length");

        assertThat(contentLength).isEqualTo("1000");
    }

    @Test
    void 헤더에_Location을_지정할_수_있다() {
        // given
        final ResponseHeaders responseHeaders = ResponseHeaders.empty();

        // when
        responseHeaders.setLocation("/index.html");

        // then
        final String location = responseHeaders.getHeaderValue("Location");

        assertThat(location).isEqualTo("/index.html");
    }

    @Test
    void 헤더에_SetCookie를_지정할_수_있다() {
        // given
        final ResponseHeaders responseHeaders = ResponseHeaders.empty();

        // when
        responseHeaders.setCookies(Cookies.from("JSESSIONID=dasfijewfun29u;name=헤나"));

        // then
        final String cookieNamesAndValues = responseHeaders.getHeaderValue("Set-Cookie");

        assertThat(cookieNamesAndValues).isEqualTo("name=헤나;JSESSIONID=dasfijewfun29u");
    }
}
