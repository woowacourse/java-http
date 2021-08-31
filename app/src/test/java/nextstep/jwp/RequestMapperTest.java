package nextstep.jwp;

import nextstep.jwp.controller.*;
import nextstep.jwp.domain.Uri;
import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.request.RequestLine;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMapperTest {

    private static RequestLine defaultLine = new RequestLine("GET", Uri.of("/"), "HTTP/1.1");
    private static RequestLine loginLine = new RequestLine("GET", Uri.of("/login"), "HTTP/1.1");;
    private static RequestLine registerLine = new RequestLine("GET", Uri.of("/register"), "HTTP/1.1");
    private static RequestLine indextLine = new RequestLine("GET", Uri.of("/index.html"), "HTTP/1.1");

    @ParameterizedTest(name = "각 uri과 일치하는 컨트롤러를 반환한다.")
    @MethodSource
    void testGetController(HttpRequest request, Controller controller) {
        //given
        //when
        Controller result = RequestMapper.getController(request);
        //then
        assertThat(result).isInstanceOf(controller.getClass());
    }

    static Stream<Arguments> testGetController() {
        return Stream.of(
                Arguments.of(HttpRequest.of(defaultLine, null, null), new DefaultController()),
                Arguments.of(HttpRequest.of(loginLine, null, null), new LoginController()),
                Arguments.of(HttpRequest.of(registerLine, null, null), new RegisterController()),
                Arguments.of(HttpRequest.of(indextLine, null, null), new IndexController()));
    }
}
