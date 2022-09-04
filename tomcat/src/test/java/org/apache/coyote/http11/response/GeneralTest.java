package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.General;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class GeneralTest {

    public static Stream<Arguments> httpStatusGeneral() {
        return Stream.of(
                Arguments.of(HttpStatus.OK, "HTTP/1.1 200 OK "),
                Arguments.of(HttpStatus.REDIRECT, "HTTP/1.1 302 FOUND ")
        );
    }

    @ParameterizedTest
    @MethodSource("httpStatusGeneral")
    void Http_Status에_따라_String이_변한다(HttpStatus httpStatus, String expected) {
        // when
        General general = new General(httpStatus);

        // then
        assertThat(general.asString()).isEqualTo(expected);
    }

}
