package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaticFileHandlerTest {

    @InjectMocks
    private StaticFileHandler staticFileHandler;

    @Mock
    private HttpRequestHandler notFoundHandler;

    @Mock
    private HttpRequest request;

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/style.css", "/script.js"})
    void 정적_파일_요청을_처리할_수_있다(String path) {
        // given
        when(request.getRequestPath()).thenReturn(path);

        // when
        boolean canHandle = staticFileHandler.canHandle(request);

        // then
        assertThat(canHandle).isTrue();
    }

    @Test
    void 정적_파일이_아닌_요청은_처리할_수_없다() {
        // given
        when(request.getRequestPath()).thenReturn("/other");

        // when
        boolean canHandle = staticFileHandler.canHandle(request);

        // then
        assertThat(canHandle).isFalse();
    }

    @Test
    void 존재하는_정적_파일_요청을_처리한다() throws IOException, URISyntaxException {
        // given
        when(request.getRequestPath()).thenReturn("/test.html");

        // when
        HttpResponse response = staticFileHandler.handle(request);
        String expectedBody = Files.readString(
                Paths.get(getClass().getClassLoader().getResource("static/test.html").toURI().getPath()),
                StandardCharsets.UTF_8
        );
        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode()),
                () -> assertThat(response.getBody().toText()).isEqualTo(expectedBody),
                () -> assertThat(response.getHeaders().get("Content-Type")).contains("text/html;charset=utf-8")
        );
    }

    @Test
    void 존재하지_않는_정적_파일_요청시_예외_발생() {
        // given
        when(request.getRequestPath()).thenReturn("/non-existent.html");

        // when & then
        assertThatThrownBy(() -> staticFileHandler.handle(request))
                .isInstanceOf(NotFoundException.class);
    }
}
