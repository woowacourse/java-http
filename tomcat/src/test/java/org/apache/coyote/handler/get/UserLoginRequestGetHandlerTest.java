package org.apache.coyote.handler.get;

import org.apache.coyote.HttpFormTestUtils;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserLoginRequestGetHandlerTest {

    @Test
    void 파일_확장자를_알_수_없고_쿼리_파라미터가_비어있을_경우에_로그인_페이지를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/login").http11().enter()
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
                .contentLength("3796").enter()
                .contentType("text/html;charset=utf-8").enter()
                .enter().responseByResource("static/login.html")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void 쿼리_파라미터를_이용해서_로그인을_성공했을_경우_메인_홈페이지로_리다이렉트한다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/login?account=gugu&password=password").http11().enter()
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
                .http11().FOUND().enter()
                .contentLength("0").enter()
                .contentType("text/html;charset=utf-8").enter()
                .location("/index.html").enter()
                .enter()
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void 쿼리_파라미터를_이용해서_로그인을_실패했을_경우_401페이지로_리다이렉트한다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/login?account=sfgd&password=password").http11().enter()
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
                .http11().FOUND().enter()
                .contentLength("0").enter()
                .contentType("text/html;charset=utf-8").enter()
                .location("/401.html").enter()
                .enter()
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }
}
