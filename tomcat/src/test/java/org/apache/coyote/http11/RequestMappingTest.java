package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.PageController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestMappingTest {

    @ParameterizedTest
    @MethodSource("existRequestAndExpect")
    @DisplayName("올바른 handlerMapping을 찾는다.")
    void find_correct_handler_mapping(final String path, final Controller expect) {
        // given
        // when
        final Controller result = RequestMapping.getController(path);

        // then
        assertThat(result).isInstanceOf(expect.getClass());
    }

    private static Stream<Arguments> existRequestAndExpect() {
        return Stream.of(
            Arguments.of("/", new RootController()),
            Arguments.of("/login", new LoginController()),
            Arguments.of("/register", new RegisterController()),
            Arguments.of("/none", new PageController())
        );
    }
}
