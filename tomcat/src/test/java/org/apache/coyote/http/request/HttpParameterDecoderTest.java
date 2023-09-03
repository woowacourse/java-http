package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpParameterDecoderTest {

    @Test
    void decode_파라미터를_디코딩한다() {
        String string = "hello=world&";
        Map<String, String> expected = Map.of("hello", "world");

        Map<String, String> actual = HttpParameterDecoder.decode(string);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void decode_형식이_올바르지_않은건_무시한다() {
        String string = "hello=world&world=";
        Map<String, String> expected = Map.of("hello", "world");

        Map<String, String> actual = HttpParameterDecoder.decode(string);

        assertThat(actual).isEqualTo(expected);
    }
}