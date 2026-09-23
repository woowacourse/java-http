package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.StubSocket;

class Http11ProcessorTest {

    @ParameterizedTest
    @CsvSource({
            "/index.html, static/index.html",
            "/css/styles.css, static/css/styles.css",
            "/login, static/login.html",
            "/login?account=gugu&password=password, static/login.html",
            "/register, static/register.html"
    })
    void 요청_대상에_맞는_정적_리소스를_반환한다(
            String requestTarget,
            String resourcePath
    ) throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET " + requestTarget + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        String expected = Files.readString(new File(resource.getFile()).toPath());

        assertThat(socket.output()).endsWith("\r\n\r\n" + expected);
    }

    @ParameterizedTest
    @CsvSource({
            "/index.html, text/html;charset=utf-8",
            "/css/styles.css, text/css;charset=utf-8"
    })
    void 리소스_확장자에_맞는_Content_Type으로_응답한다(
            String requestTarget,
            String contentType
    ) {
        // given
        String httpRequest = String.join("\r\n",
                "GET " + requestTarget + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Content-Type: " + contentType);
    }

    @ParameterizedTest
    @CsvSource({
            "gugu, password, /index.html",
            "unknown, password, /401.html",
            "gugu, wrong, /401.html"
    })
    void 로그인_결과에_맞는_경로로_리다이렉트한다(
            String account,
            String password,
            String location
    ) {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=" + account + "&password=" + password + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: " + location);
    }
}
