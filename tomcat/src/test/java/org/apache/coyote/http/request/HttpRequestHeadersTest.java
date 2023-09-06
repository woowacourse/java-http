package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestHeadersTest {

    @Test
    void from_메서드는_유효한_헤더를_전달하면_HttpRequestHeaders를_초기화한다() {
        final String headerContents = "Content-Type: application/json";

        final HttpRequestHeaders actual = HttpRequestHeaders.from(headerContents);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.findValue("Content-Type")).isEqualTo("application/json");
        });
    }

    @Test
    void isRequestBodyEmpty_메서드는_바디가_없을_경우_true를_반환한다() {
        final String headerContents = "Content-Type: application/json";
        final HttpRequestHeaders headers = HttpRequestHeaders.from(headerContents);

        final boolean actual = headers.isRequestBodyEmpty();

        assertThat(actual).isTrue();
    }

    @Test
    void isRequestBodyEmpty_메서드는_바디가_있을_경우_false를_반환한다() {
        final String headerContents = "Content-Length: 12";
        final HttpRequestHeaders headers = HttpRequestHeaders.from(headerContents);

        final boolean actual = headers.isRequestBodyEmpty();

        assertThat(actual).isFalse();
    }

    @Test
    void findValue_메서드는_key를_전달하면_value를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: application/json");

        final String actual = headers.findValue("Content-Type");

        assertThat(actual).isEqualTo("application/json");
    }
}
