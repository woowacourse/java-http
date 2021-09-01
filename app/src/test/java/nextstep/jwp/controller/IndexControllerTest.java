package nextstep.jwp.controller;

import nextstep.jwp.domain.Uri;
import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.request.RequestLine;
import nextstep.jwp.domain.response.HttpResponse;
import nextstep.jwp.domain.response.HttpStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest {


    @ParameterizedTest(name = "Indexcontroller Service 테스트")
    @MethodSource
    void service(String uri, HttpStatus statusCode) {
        //given
        RequestLine requestLine = new RequestLine("GET", Uri.of(uri), "HTTP/1.1");
        HttpRequest request = HttpRequest.of(requestLine, null, null);
        IndexController controller = new IndexController();
        //when
        HttpResponse response = controller.service(request);
        //then
        assertThat(response.getHttpStatus()).isEqualTo(statusCode);
    }

    static Stream<Arguments> service() {
        return Stream.of(
                Arguments.of("/index.html", HttpStatus.OK),
                Arguments.of("/400.html", HttpStatus.BAD_REQUEST),
                Arguments.of("/401.html", HttpStatus.UNAUTHORIZED),
                Arguments.of("/404.html", HttpStatus.NOT_FOUND));
    }
}
