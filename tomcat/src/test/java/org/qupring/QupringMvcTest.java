package org.qupring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpTomcatRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.response.HttpTomcatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qupring.mvc.annotation.Route;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.QupringMvc;
import org.qupring.mvc.handler.HandlerMapping;

class QupringMvcTest {

    private HandlerMapping handlerMapping;
    private QupringMvc qupringMvc;

    @BeforeEach
    void setUp() {
        handlerMapping = new HandlerMapping();
        qupringMvc = new QupringMvc(handlerMapping);
    }

    @Test
    void 루트_경로에_기본_본문을_응답한다() {
        // given
        HttpTomcatResponse response = HttpTomcatResponse.createDefault();

        // when
        qupringMvc.run(request("/", HttpMethod.GET), response);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello world!");
        assertThat(response.getHeader("Content-Length")).isEqualTo("12");
    }

    @Test
    void 매핑된_정적_리소스를_응답한다() {
        // given
        handlerMapping.addResourceMappings(
                Map.of("/login", "static/login.html")
        );
        HttpTomcatResponse response = HttpTomcatResponse.createDefault();

        // when
        qupringMvc.run(request("/login", HttpMethod.GET), response);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody())
                .isEqualTo(HtmlReader.read("static/login.html"));
        assertThat(response.getHeader("Content-Type"))
                .isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void 컨트롤러를_정적_리소스보다_먼저_실행한다() {
        // given
        handlerMapping.addControllerMappings(List.of(TestController.class));
        handlerMapping.addResourceMappings(
                Map.of("/test", "static/login.html")
        );
        HttpTomcatResponse response = HttpTomcatResponse.createDefault();

        // when
        qupringMvc.run(request("/test", HttpMethod.GET), response);

        // then
        assertThat(response.getBody()).isEqualTo("controller response");
    }

    @Test
    void 매핑이_없으면_404를_응답한다() {
        // given
        HttpTomcatResponse response = HttpTomcatResponse.createDefault();

        // when
        qupringMvc.run(request("/unknown", HttpMethod.GET), response);

        // then
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getBody())
                .isEqualTo(HtmlReader.read("static/404.html"));
        assertThat(response.getHeader("Content-Type"))
                .isEqualTo("text/html;charset=utf-8");
    }

    private HttpRequest request(String path, HttpMethod method) {
        return new HttpTomcatRequest(
                method,
                path,
                "HTTP/1.1",
                null,
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of()
        );
    }

    public static class TestController {

        @Route(path = "/test", method = HttpMethod.GET)
        public void test(HttpRequest request, HttpResponse response) {
            response.setBody("controller response");
        }
    }
}
