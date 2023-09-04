package org.apache.coyote.handler;

import org.apache.coyote.request.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MappingInfoTest {

    @Test
    void HttpMethod와_RequestPath를_이용하여_생성한다() {
        // given
        final String httpMethodValue = HttpMethod.GET.name();
        final String requestPath = "/index.html";

        // expect
        assertThatCode(() -> new MappingInfo(httpMethodValue, requestPath))
                .doesNotThrowAnyException();
    }

    @Test
    void HttpMethod와_RequestPath_값이_같을_경우_동일성을_보장한다() {
        // given
        final String httpMethodValue = HttpMethod.GET.name();
        final String requestPath = "/index.html";

        // when
        final MappingInfo actual = new MappingInfo(httpMethodValue, requestPath);
        final MappingInfo expected = new MappingInfo("GET", "/index.html");

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
