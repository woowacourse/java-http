package nextstep.jwp.dispatcher.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.parser.HttpParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileAccessHandlerMappingTest {

    private HandlerMapping handlerMapping;

    @BeforeEach
    void setUp() {
        handlerMapping = new FileAccessHandlerMapping();
    }

    @DisplayName("HTTP 요청 URI에 파일 확장자가 존재하면 support는 true를 반환한다.")
    @Test
    void support_HTTP_요청_파일확장자_포함() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when
        HttpRequest httpRequest = HttpParser.parse(inputStream);
        boolean actual = handlerMapping.supports(httpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("HTTP 요청 URI에 파일 확장자가 존재하지 않으면 support는 false를 반환한다.")
    @Test
    void support_HTTP_요청_파일확장자_미포함() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "GET /index HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when
        HttpRequest httpRequest = HttpParser.parse(inputStream);
        boolean actual = handlerMapping.supports(httpRequest);

        // then
        assertThat(actual).isFalse();
    }
}
