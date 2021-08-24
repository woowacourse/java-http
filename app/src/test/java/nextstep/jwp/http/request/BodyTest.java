package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import nextstep.jwp.fixture.Fixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BodyTest {

    @DisplayName("HttpHeader에서 Body를 추출한다.")
    @ParameterizedTest
    @MethodSource("parametersForGetBody")
    void getBody(String httpRequest, String expected) {
        Body body = new Body(httpRequest);

        body.getBody()
            .ifPresentOrElse(
                value -> assertThat(value).isEqualTo(expected),
                () -> assertThat(expected).isNull()
            );
    }

    private static Stream<Arguments> parametersForGetBody() {
        return Stream.of(
            Arguments.of(Fixture.getHttpRequest(), null),
            Arguments.of(Fixture.postHttpRequest("test"), "test"),
            Arguments.of(Fixture.putHttpRequest("test"), "test"),
            Arguments.of(Fixture.deleteHttpRequest(), null)
        );
    }
}