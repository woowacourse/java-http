package org.apache.http;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HttpMethodTest {

    @ParameterizedTest
    @CsvSource(value = {"GET /index.html,true", "NO /index.html,false"})
    void 문자열이_HttpMethod로_시작한다면_해당_HttpMethod를_반환한다(final String line, final boolean expected) {
        // given
        final Optional<HttpMethod> httpMethod = HttpMethod.find(line);
        // when, then
        assertThat(httpMethod.isPresent()).isEqualTo(expected);
    }
}
