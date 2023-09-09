package nextstep.jwp.presentation;

import org.apache.coyote.HttpFormTestUtils;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RegisterPageControllerTest {

    @Test
    void 파일_확장자를_알_수_없으면서_GET일_경우_회원가입_페이지를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/register").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .enter()
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().OK().enter()
                .contentLength("4319").enter()
                .contentType("text/html;charset=utf-8").enter()
                .enter().responseByResource("static/register.html")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void 요청_바디에_회원_정보를_넣어_회원가입_요청을_성공하면_메인_홈페이지로_리다이렉트한다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .POST().requestUri("/register").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("80").enter()
                .contentType("application/x-www-form-urlencoded").enter()
                .accept("*/*").enter()
                .enter()
                .requestBody("account=hyena&email=test@test.com&password=test4312")
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().FOUND().enter()
                .location("/index.html").enter()
                .setCookie("JSESSIONID=")
                .build();

        assertThat(socket.output()).contains(httpResponse);
    }
}
