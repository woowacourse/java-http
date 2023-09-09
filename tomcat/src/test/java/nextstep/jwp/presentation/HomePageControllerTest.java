package nextstep.jwp.presentation;

import org.apache.coyote.HttpFormTestUtils;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HomePageControllerTest {

    @Test
    void HelloWorld_페이지를_응답한다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/html;charset=utf-8").enter()
                .enter()
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().OK().enter()
                .contentLength("12").enter()
                .contentType("text/html;charset=utf-8").enter()
                .enter().responseBody("Hello world!")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }
}
