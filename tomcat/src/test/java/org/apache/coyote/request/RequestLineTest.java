package org.apache.coyote.request;

import org.apache.coyote.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestLineTest {

    @Test
    void HttpMethod와_URI와_HttpVersion의_값이_들어있는_요청_라인_값을_이용해서_생성에_성공한다() {
        // given
        final String requestLineValue = "GET /index.html HTTP/1.1";

        // expect
        assertThatCode(() -> RequestLine.from(requestLineValue))
                .doesNotThrowAnyException();
    }

    @Test
    void 요청_라인의_필드값인_HttpMethod_HttpVersion_RequestPath_QueryParams를_가져올_수_있다() {
        // given
        final String requestLineValue = "GET /index.html HTTP/1.1";

        // when
        final RequestLine actual = RequestLine.from(requestLineValue);

        final HttpMethod expectedHttpMethod = HttpMethod.GET;
        final HttpVersion expectedHttpVersion = HttpVersion.HTTP_1_1;
        final RequestPath expectedRequestPath = new RequestPath("/index.html");
        final QueryParams expectedQueryParams = QueryParams.empty();

        // then
        assertAll(
                () -> assertThat(actual.httpMethod()).isEqualTo(expectedHttpMethod),
                () -> assertThat(actual.httpVersion()).isEqualTo(expectedHttpVersion),
                () -> assertThat(actual.requestPath()).isEqualTo(expectedRequestPath),
                () -> assertThat(actual.queryParams()).isEqualTo(expectedQueryParams)
        );
    }

    @Test
    void URI에_쿼리_파라미터가_있을_경우_요청_패스와_쿼리_파라미터를_파싱하여_생성할_수_있다() {
        // given
        final String requestLineValue = "GET /index.html?account=hyena HTTP/1.1";

        // when
        final RequestLine actual = RequestLine.from(requestLineValue);

        final RequestPath expectedRequestPath = new RequestPath("/index.html");
        final QueryParams expectedQueryParams = QueryParams.from("account=hyena");

        // then
        assertAll(
                () -> assertThat(actual.requestPath()).isEqualTo(expectedRequestPath),
                () -> assertThat(actual.queryParams()).isEqualTo(expectedQueryParams)
        );
    }
}
