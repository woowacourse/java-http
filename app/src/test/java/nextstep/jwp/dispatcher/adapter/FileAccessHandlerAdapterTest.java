package nextstep.jwp.dispatcher.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.dispatcher.mapping.FileAccessHandlerMapping;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.dispatcher.mapping.HttpRequestHandlerMapping;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpResponseImpl;
import nextstep.jwp.http.parser.HttpParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileAccessHandlerAdapterTest {

    private HandlerAdapter handlerAdapter;

    @BeforeEach
    void setUp() {
        handlerAdapter = new FileAccessHandlerAdapter();
    }

    @DisplayName("FileAccessHandlerMapping(파일 접근 핸들러 매핑)을 처리할 수 있다.")
    @Test
    void supports_FileAccessHandlerMapping_요청() {
        // given
        HandlerMapping handlerMapping = new FileAccessHandlerMapping();

        // when, then
        assertThat(handlerAdapter.supports(handlerMapping)).isTrue();
    }

    @DisplayName("FileAccessHandlerMapping(파일 접근 핸들러 매핑)외의 HandlerMapping은 처리할 수 없다.")
    @Test
    void supports_FileAccessHandlerMapping_이외_요청() {
        // given
        HandlerMapping handlerMapping = new HttpRequestHandlerMapping();

        // when, then
        assertThat(handlerAdapter.supports(handlerMapping)).isFalse();
    }

    @DisplayName("요청한 파일을 찾아 Response 바디에 담고, 적절한 Content-Type을 찾아 헤더에 추가할 수 있다.")
    @Test
    void handle_정적파일_요청() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = HttpParser.parse(inputStream);
        HttpResponse response = new HttpResponseImpl();

        // when
        HandlerMapping handlerMapping = new FileAccessHandlerMapping();
        handlerAdapter.handle(request, response, handlerMapping.getHandler(request));

        // then
        assertThat(response.getStatusAsString()).isEqualTo("200");
        assertThat(response.getHeaders().getHeaderByName("Content-Type"))
            .get()
            .isEqualTo("text/css ");
    }

    @DisplayName("요청한 파일이 존재하지 않을 경우, 예외가 던져진다.")
    @Test
    void handle_존재하지_않는_정적파일_요청() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "GET /css/styles_binghe.css HTTP/1.1",
            "Host: localhost:8080",
            "Accept: text/css,*/*;q=0.1",
            "Connection: keep-alive",
            ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = HttpParser.parse(inputStream);
        HttpResponse response = new HttpResponseImpl();

        // when
        HandlerMapping handlerMapping = new FileAccessHandlerMapping();
        Handler handler = handlerMapping.getHandler(request);

        // then
        assertThatThrownBy(() -> {
            handlerAdapter.handle(request, response, handler);
        }).isInstanceOf(RuntimeException.class);
    }
}
