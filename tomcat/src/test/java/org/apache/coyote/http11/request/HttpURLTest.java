package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpURLTest {

    @DisplayName("쿼리 파라미터가 존재하지 않으면 path만 파싱한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/path/to/file", "/path/to/file?"})
    void httpURLWithOutQueryParams(String url) {
        // when
        HttpURL httpURL = HttpURL.from(url);

        // then
        assertAll(
                () -> assertThat(httpURL.fullUrl()).isEqualTo(url),
                () -> assertThat(httpURL.path()).isEqualTo("/path/to/file"),
                () -> assertThat(httpURL.queryParameters()).isEmpty()
        );
    }

    @DisplayName("쿼리 파라미터가 존재하면 각 파라미터를 포함하여 파싱한다.")
    @Test
    void httpURLWithQueryParams() {
        // given
        String url = "/path/to/file?query1=1&query2=2";

        // when
        HttpURL httpURL = HttpURL.from(url);

        // then
        assertAll(
                () -> assertThat(httpURL.fullUrl()).isEqualTo(url),
                () -> assertThat(httpURL.path()).isEqualTo("/path/to/file"),
                () -> assertThat(httpURL.queryParameters()).containsEntry("query1", "1"),
                () -> assertThat(httpURL.queryParameters()).containsEntry("query2", "2")
        );
    }

}
