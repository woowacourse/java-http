package nextstep.org.apache.coyote.http11;

import static org.apache.coyote.http11.common.ContentType.CSS;
import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.header.HeaderName.ACCEPT;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.FrontController;
import org.apache.coyote.http11.common.Status;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class FrontControllerTest {

    @DisplayName("/로 GET 요청을 보내면 Hello world!를 반환한다.")
    @Test
    void handleRoot() throws IOException {
        final Response response = FrontController.handle(
                Request.of("get", "/", Map.of(ACCEPT, "text/html"), "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).contains(HTML.toString());
        assertThat(response.getBody()).isEqualTo("Hello world!");
    }

    @DisplayName("html 파일명을 자원으로 GET 요청을 보내면 resources/static 디렉토리 내의 동일한 파일을 찾아 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "/index.html", "/login.html", "/register.html", "/401.html"
    })
    void handleHTML(final String URI) throws IOException {
        final Response response = FrontController.handle(
                Request.of("get", URI, Map.of(ACCEPT, HTML.toString()), "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).isEqualTo(HTML.toString());
    }

    @DisplayName("css 파일명을 자원으로 GET 요청을 보내면 resources/static 디렉토리 내의 동일한 파일을 찾아 반환한다.")
    @Test
    void handleCSS() throws IOException {
        final Response response = FrontController.handle(
                Request.of("get", "/css/styles.css", Map.of(ACCEPT, CSS.toString()), "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).isEqualTo(CSS.toString());
    }

    @DisplayName("resources/static 디렉토리 내에 존재하지 않는 파일명을 자원으로 GET 요청을 보내면 404 응답코드로 반환한다.")
    @Test
    void handleNotFound() throws IOException {
        final Response response = FrontController.handle(
                Request.of("get", "/neverexist/not.css", Map.of(ACCEPT, HTML.toString()), "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND);
    }

}
