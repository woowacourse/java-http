package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.List;
import java.util.stream.Stream;
import org.apache.coyote.http11.data.HttpMethod;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpVersion;
import org.apache.coyote.http11.resource.ResourceHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestMappingTest {

    @ParameterizedTest
    @MethodSource("test")
    @DisplayName("uri로 컨트롤러를 찾는다.")
    void getController(String path, Controller expectdController) {
        // given
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, path, HttpVersion.HTTP_1_1, null, null, null, null,
                List.of());

        // when
        Controller actualController = RequestMapping.getController(httpRequest);

        // then
        assertThat(actualController).isEqualTo(expectdController);
    }

    private static Stream<Arguments> test() {
        return Stream.of(
                Arguments.of("/login", LoginController.getInstance()),
                Arguments.of("/register", RegisterController.getInstance()),
                Arguments.of("/400.html", ResourceHandler.getInstance())
        );
    }
}
