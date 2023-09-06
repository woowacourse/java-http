package org.apache.coyote.domain;

import static org.apache.coyote.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.Url;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void HttpRequest는_어떤_요청_메서드인지_검사할_수_있다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/index.html"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // expected
        assertThat(request.isRequestMethodOf(GET)).isTrue();
    }

    @Test
    void HttpRequest는_특정_문자열을_포함하는지_검사할_수_있다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/js/function.js"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // expected
        assertThat(request.isContainsSubStringInUrl(".js")).isTrue();
    }
}
