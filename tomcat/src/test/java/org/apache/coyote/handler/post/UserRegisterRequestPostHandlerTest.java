package org.apache.coyote.handler.post;

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
class UserRegisterRequestPostHandlerTest {

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
                .contentLength("0").enter()
                .contentType("text/html;charset=utf-8").enter()
                .location("/index.html").enter()
                .enter()
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }
}
