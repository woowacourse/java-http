package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.enums.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private static final HttpRequestBody EMPTY_REQUEST_BODY = new HttpRequestBody("");

    @DisplayName(value = "QueryString이 존재하지 않는 경우 isQueryStringEmpty는 true")
    @Test
    void isQueryStringEmpty() {
        // given
        final HttpRequest httpRequest = new HttpRequest("GET /login HTTP/1.1 ", EMPTY_REQUEST_BODY);

        // when
        final boolean actual = httpRequest.isQueryStringEmpty();

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName(value = "같은 HttpMethod인 경우 isSameHttpMethod는 true")
    @Test
    void isSameHttpMethod() {
        // given
        final HttpRequest httpRequest = new HttpRequest("GET /login HTTP/1.1 ", EMPTY_REQUEST_BODY);

        // when & then
        assertThat(httpRequest.isSameHttpMethod(HttpMethod.GET)).isTrue();
    }
}
