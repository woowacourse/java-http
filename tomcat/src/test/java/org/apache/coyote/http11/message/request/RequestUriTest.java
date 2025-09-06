package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RequestUriTest {

    @Test
    void 요청_uri로_생성한다() {
        // given
        String uri = "/path";
        RequestUri requestUri = RequestUri.from(uri);

        // when
        String actual = requestUri.getPath();

        // then
        assertThat(actual).isEqualTo(uri);
    }

    @Test
    void 요청_url으로_생성시_요청_경로와_쿼리_파라미터를_갖는다() {
        // given
        String uri = "/path?a=1&b=2";
        RequestUri requestUri = RequestUri.from(uri);

        // when
        String path = requestUri.getPath();
        Map<String, String> queryParams = requestUri.getQueryParams();

        // then
        assertAll(
                () -> assertThat(path).isEqualTo("/path"),
                () -> assertThat(queryParams).containsEntry("a", "1"),
                () -> assertThat(queryParams).containsEntry("b", "2")
        );
    }
}
