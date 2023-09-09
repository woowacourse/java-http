package org.apache.coyote.publisher;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.request.RequestPath;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestLinePublisherTest {

    @Test
    void 생성에_성공한다() {
        // given
        final String requestLineValue = "GET /index.html HTTP/1.1";

        // expect
        assertThatCode(() -> RequestLinePublisher.read(requestLineValue))
                .doesNotThrowAnyException();
    }

    @Test
    void 요청_라인의_필드값인_HttpMethod_HttpVersion_RequestPath_QueryParams를_가져올_수_있다() {
        // given
        final String requestLineValue = "GET /index.html HTTP/1.1";

        // when
        final RequestLine actual = RequestLinePublisher.read(requestLineValue).toRequestLine();

        // then
        final HttpMethod expectedHttpMethod = HttpMethod.GET;
        final HttpVersion expectedHttpVersion = HttpVersion.HTTP_1_1;
        final RequestPath expectedRequestPath = new RequestPath("/index.html");
        final QueryParams expectedQueryParams = QueryParams.empty();

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
        final RequestLine actual = RequestLinePublisher.read(requestLineValue).toRequestLine();

        // then
        final RequestPath expectedRequestPath = new RequestPath("/index.html");
        final QueryParams expectedQueryParams = QueryParams.from("account=hyena");

        assertAll(
                () -> assertThat(actual.requestPath()).isEqualTo(expectedRequestPath),
                () -> assertThat(actual.queryParams()).isEqualTo(expectedQueryParams)
        );
    }
}
