package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpParametersTest {

    @Test
    void decode_파라미터를_디코딩한다() {
        String string = "hello=world&";
        Map<String, String> expected = Map.of("hello", "world");

        HttpParameters httpParameters = HttpParameters.from(string);

        assertThat(httpParameters.getParameters()).isEqualTo(expected);
    }

    @Test
    void decode_형식이_올바르지_않은건_무시한다() {
        String string = "hello=world&world=";
        Map<String, String> expected = Map.of("hello", "world");

        HttpParameters httpParameters = HttpParameters.from(string);

        assertThat(httpParameters.getParameters()).isEqualTo(expected);
    }
}