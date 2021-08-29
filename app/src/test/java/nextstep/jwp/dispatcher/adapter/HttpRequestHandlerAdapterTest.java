package nextstep.jwp.dispatcher.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.context.ApplicationContextImpl;
import nextstep.jwp.dispatcher.mapping.FileAccessHandlerMapping;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.dispatcher.mapping.HttpRequestHandlerMapping;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpResponseImpl;
import nextstep.jwp.http.parser.HttpParser;
import nextstep.jwp.mock.MockHttpHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHandlerAdapterTest {

    private HandlerAdapter handlerAdapter;

    @BeforeEach
    void setUp() {
        handlerAdapter = new HttpRequestHandlerAdapter();
    }

    @DisplayName("HttpRequestHandlerMapping(일반 요청 핸들러 매핑)을 처리할 수 있다.")
    @Test
    void supports_HttpRequestHandlerMapping_요청() {
        // given
        HandlerMapping handlerMapping = new HttpRequestHandlerMapping();

        // when, then
        assertThat(handlerAdapter.supports(handlerMapping)).isTrue();
    }

    @DisplayName("HttpRequestHandlerMapping(일반 요청 핸들러 매핑)이외 요청은 처리할 수 없다.")
    @Test
    void supports_HttpRequestHandlerMapping_이외_요청() {
        // given
        HandlerMapping handlerMapping = new FileAccessHandlerMapping();

        // when, then
        assertThat(handlerAdapter.supports(handlerMapping)).isFalse();
    }

    @DisplayName("요청한 URI에 대한 비즈니스 로직을 처리하고, Response에 처리 결과를 저장한다. - GET")
    @Test
    void handle_일반_비즈니스로직_요청_GET() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "GET /binghe HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        ApplicationContext applicationContext = new ApplicationContextImpl();
        applicationContext.addHandler("/binghe", new MockHttpHandler());

        HttpRequest request = HttpParser.parse(inputStream);
        request.setApplicationContext(applicationContext);
        HttpResponse response = new HttpResponseImpl();

        // when
        HandlerMapping handlerMapping = new HttpRequestHandlerMapping();
        handlerAdapter.handle(request, response, handlerMapping.getHandler(request));

        // then
        assertThat(response.getContentAsString()).isNotBlank();
        assertThat(response.getStatusAsString()).isEqualTo("200");
    }

    @DisplayName("요청한 URI에 대한 비즈니스 로직을 처리하고, Response에 처리 결과를 저장한다. - POST")
    @Test
    void handle_일반_비즈니스로직_요청_POST() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "POST /binghe HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 14",
            "",
            "hi~ binghe~~~~"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        ApplicationContext applicationContext = new ApplicationContextImpl();
        applicationContext.addHandler("/binghe", new MockHttpHandler());

        HttpRequest request = HttpParser.parse(inputStream);
        request.setApplicationContext(applicationContext);
        HttpResponse response = new HttpResponseImpl();

        // when
        HandlerMapping handlerMapping = new HttpRequestHandlerMapping();
        handlerAdapter.handle(request, response, handlerMapping.getHandler(request));

        // then
        assertThat(response.getContentAsString()).isNotBlank();
        assertThat(response.getStatusAsString()).isEqualTo("200");
    }
}
