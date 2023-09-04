package org.apache.coyote.handler;

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
class ResourceRequestHandlerTest {

    @Test
    void index_html_메인_홈페이지를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/index.html").http11().enter()
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
                .contentLength("5564").enter()
                .contentType("text/html;charset=utf-8").enter()
                .enter().responseByResource("static/index.html")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void login_html_로그인_페이지를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/login.html").http11().enter()
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
                .contentLength("3796").enter()
                .contentType("text/html;charset=utf-8").enter()
                .enter().responseByResource("static/login.html")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void register_html_회원가입_페이지를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/register.html").http11().enter()
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
                .contentLength("4319").enter()
                .contentType("text/html;charset=utf-8").enter()
                .enter().responseByResource("static/register.html")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void 권한_오류_401_페이지_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/401.html").http11().enter()
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
                .contentLength("2426").enter()
                .contentType("text/html;charset=utf-8").enter()
                .enter().responseByResource("static/401.html")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void chart_area_js를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/assets/chart-area.js").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("application/javascript").enter()
                .enter()
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().OK().enter()
                .contentLength("1530").enter()
                .contentType("application/javascript;charset=utf-8").enter()
                .enter().responseByResource("static/assets/chart-area.js")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void chart_bar_js를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/assets/chart-bar.js").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("application/javascript").enter()
                .enter()
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().OK().enter()
                .contentLength("1112").enter()
                .contentType("application/javascript;charset=utf-8").enter()
                .enter().responseByResource("static/assets/chart-bar.js")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void chart_pie_js를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/assets/chart-pie.js").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("application/javascript").enter()
                .enter()
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().OK().enter()
                .contentLength("597").enter()
                .contentType("application/javascript;charset=utf-8").enter()
                .enter().responseByResource("static/assets/chart-pie.js")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void scripts_js를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/js/scripts.js").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("application/javascript").enter()
                .enter()
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().OK().enter()
                .contentLength("976").enter()
                .contentType("application/javascript;charset=utf-8").enter()
                .enter().responseByResource("static/js/scripts.js")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @Test
    void styles_css를_응답한다() throws IOException {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/css/styles.css").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/css").enter()
                .enter()
                .build();

        final StubSocket socket = new StubSocket(httpRequest);
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String httpResponse = HttpFormTestUtils.builder()
                .http11().OK().enter()
                .contentLength("211991").enter()
                .contentType("text/css;charset=utf-8").enter()
                .enter().responseByResource("static/css/styles.css")
                .build();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }
}
