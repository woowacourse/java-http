package nextstep.jwp.dispatcher.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.context.ApplicationContextImpl;
import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.dispatcher.handler.HttpHandler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.parser.HttpParser;
import nextstep.jwp.mock.MockHttpHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHandlerMappingTest {

    private HandlerMapping handlerMapping;

    @BeforeEach
    void setUp() {
        handlerMapping = new HttpRequestHandlerMapping();
    }

    @DisplayName("HTTP 요청 URI에 파일 확장자가 존재하지 않으면 support는 true를 반환한다.")
    @Test
    void support_HTTP_요청_URI_파일확장자_포함() {
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
        assertThat(actual).isTrue();
    }

    @DisplayName("HTTP 요청 URI에 파일 확장자가 존재하면 support는 false를 반환한다.")
    @Test
    void support_HTTP_요청_URI_파일확장자_미포함() {
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
        assertThat(actual).isFalse();
    }

    @DisplayName("요청한 URL에 매칭되는 핸들러를 찾아서 반환할 수 있다.")
    @Test
    void getHandler_HTTP_요청_URI_매칭() {
        // given
        Handler handler = new HttpHandler() {};
        ApplicationContext applicationContext = new ApplicationContextImpl();
        applicationContext.addHandler("/index", handler);
        String httpRequestMessage = String.join("\r\n",
            "GET /index HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when
        HttpRequest httpRequest = HttpParser.parse(inputStream);
        httpRequest.setApplicationContext(applicationContext);

        Handler actual = handlerMapping.getHandler(httpRequest);

        // then
        assertThat(actual).isSameAs(handler);
    }

    @DisplayName("요청한 URL에 맞는 핸들러가 없으면 예외가 발생한다.")
    @Test
    void getHandler_HTTP_요청_매칭되는_URL이_없는_경우() {
        // given
        Handler handler = new MockHttpHandler();
        ApplicationContext applicationContext = new ApplicationContextImpl();
        applicationContext.addHandler("/index", handler);
        String httpRequestMessage = String.join("\r\n",
            "GET /binghe HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when
        HttpRequest request = HttpParser.parse(inputStream);
        request.setApplicationContext(applicationContext);

        // then
        assertThatThrownBy(() -> handlerMapping.getHandler(request))
            .isInstanceOf(RuntimeException.class);
    }
}
