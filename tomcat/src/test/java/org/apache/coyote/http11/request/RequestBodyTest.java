package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RequestBodyTest {

    static Stream<Arguments> testDataForGetUserInformation() {
        return Stream.of(
                Arguments.of("account=gugu&password=password", Map.of("account", "gugu", "password", "password")),
                Arguments.of("account=gugu", Map.of("account", "gugu")),
                Arguments.of("account=gugu&password=", Map.of("account", "gugu")),
                Arguments.of("account=password=password", Map.of()),
                Arguments.of("account", Map.of()),
                Arguments.of("", Map.of())
        );
    }

    @DisplayName("RequestBody에서 유저 정보를 추출한다. 정보가 비어있으면 담지 않고, 형식이 잘못되었으면 빈 맵을 반환한다.")
    @MethodSource("testDataForGetUserInformation")
    @ParameterizedTest
    void testGetUserInformation(String rawRequestBody, Map<String, String> expected) {
        // given
        final RequestBody requestBody = new RequestBody();
        requestBody.setBody(rawRequestBody);

        // when
        Map<String, String> actual = requestBody.getUserInformation();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
