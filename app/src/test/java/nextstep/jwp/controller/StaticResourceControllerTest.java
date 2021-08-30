package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.StaticResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private StaticResourceController staticResourceController;
    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        StaticResourceService staticResourceService = new StaticResourceService();

        staticResourceController = new StaticResourceController(staticResourceService);
    }

    @DisplayName("정적 자원 탐색에 실패할 경우, '/404.html'을 반환한다.")
    @Test
    void notFoundFile() throws IOException {
        // given
        String requestString = String.join(NEW_LINE, "POST /no-exist-file HTTP/1.1", "", "");
        try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
            StandardCharsets.UTF_8))) {
            httpRequest = HttpRequest.parse(inputStream);
        }

        String expectString = "HTTP/1.1 404 Not Found \n"
            + "Content-Length: 9 \n"
            + "Content-Type: text/html; charset=UTF-8 \n"
            + "\n"
            + "NOT FOUND";

        // when
        HttpResponse httpResponse = staticResourceController.doService(httpRequest);

        // then
        assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
    }

    @DisplayName("메서드에 관계없이 정적자원을 반환한다.")
    @Nested
    class NoCareMethod {

        @DisplayName("GET 요청")
        @Test
        void getMethod() throws IOException {
            // given
            String requestString = String.join(NEW_LINE, "POST /static-page.html HTTP/1.1", "", "");
            try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                StandardCharsets.UTF_8))) {
                httpRequest = HttpRequest.parse(inputStream);
            }

            String expectString = "HTTP/1.1 200 OK \n"
                + "Content-Length: 20 \n"
                + "Content-Type: text/html; charset=UTF-8 \n"
                + "\n"
                + "static page is good!";

            // when
            HttpResponse httpResponse = staticResourceController.doService(httpRequest);

            // then
            assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
        }

        @DisplayName("POST 요청")
        @Test
        void postMethod() throws IOException {
            // given
            String requestString = String.join(NEW_LINE, "POST /static-page.html HTTP/1.1", "", "");
            try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                StandardCharsets.UTF_8))) {
                httpRequest = HttpRequest.parse(inputStream);
            }

            String expectString = "HTTP/1.1 200 OK \n"
                + "Content-Length: 20 \n"
                + "Content-Type: text/html; charset=UTF-8 \n"
                + "\n"
                + "static page is good!";

            // when
            HttpResponse httpResponse = staticResourceController.doService(httpRequest);

            // then
            assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
        }
    }
}
