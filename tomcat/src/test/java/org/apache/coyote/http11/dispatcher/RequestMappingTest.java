package org.apache.coyote.http11.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RequestMappingTest {

    static Stream<Arguments> testDataForGetController() {
        return Stream.of(
                Arguments.of("/login", LoginController.class),
                Arguments.of("/register", RegisterController.class),
                Arguments.of("/index", AbstractController.class),
                Arguments.of("/styles", AbstractController.class)
        );
    }

    @DisplayName("Request Url에 맞는 Controller를 대응시킨다.")
    @MethodSource("testDataForGetController")
    @ParameterizedTest
    void testGetController(final String requestUrl, final Class<? extends Controller> expected) {
        // given
        RequestMapping requestMapping = new RequestMapping();

        // when
        final Controller actual = requestMapping.getController(requestUrl);

        // then
        assertThat(actual.getClass()).isEqualTo(expected);
    }
}
