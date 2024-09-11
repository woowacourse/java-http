package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.component.FileExtension;
import org.apache.coyote.http11.component.MediaType;
import org.apache.coyote.http11.fixture.HttpRequestFixture;
import org.apache.coyote.http11.fixture.HttpResponseFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("루트 경로로 HTTP 요청을 보내면 index.html이 출력된다.")
    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        File file = new File(Objects.requireNonNull(resource).getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        var expected = HttpResponseFixture.getGetResponse(body, MediaType.TEXT_HTML.getValue());
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET 요청을 보내면 HTTP 응답과 해당하는 파일을 파일 출력을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/login.html", "/register.html", "/css/styles.css", "/assets/chart-area.js"})
    void processGetFile(String path) throws IOException {
        // given
        final String httpRequest = HttpRequestFixture.getGetRequestMessage(path);
        int lastIndex = path.lastIndexOf(".");
        FileExtension extension = FileExtension.from(path.substring(lastIndex));
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        String body = new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));
        var expected = HttpResponseFixture.getGetResponse(body, extension.getMediaType());
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("확장자 없이 path를 요청하면 html 파일을 탐색한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/index", "/login", "/register"})
    void processWithoutExtension(String path) throws IOException {
        //given
        String httpRequest = HttpRequestFixture.getGetRequestMessage(path);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);

        //then
        URL resource = getClass().getClassLoader().getResource("static" + path + ".html");
        File file = new File(Objects.requireNonNull(resource).getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        var expected = HttpResponseFixture.getGetResponse(body, MediaType.TEXT_HTML.getValue());
        assertThat(socket.output()).isEqualTo(expected);
    }
}
