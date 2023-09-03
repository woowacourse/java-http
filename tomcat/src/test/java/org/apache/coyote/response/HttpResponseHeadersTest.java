package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.common.ContentType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseHeadersTest {

    @Test
    void 헤더를_추가한다() {
        final HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders();

        httpResponseHeaders.addHeader("Content-Type", ContentType.APPLICATION_JSON.getType());

        assertThat(httpResponseHeaders.toString()).contains("Content-Type: application/json");
    }

    @Test
    void 쿠키를_추가한다() {
        final HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders();

        httpResponseHeaders.addCookie("JSESSIONID", String.valueOf(123));

        assertThat(httpResponseHeaders.toString()).contains("Set-Cookie: JSESSIONID=123");
    }
}
