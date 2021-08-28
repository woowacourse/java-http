package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.infrastructure.http.HttpHeaders.Builder;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    private ViewResolver viewResolver;

    @BeforeEach
    void setUp() {
        viewResolver = new ViewResolver("static");
    }

    @DisplayName("존재하지 않는 경로일 경우 404.html로 응답")
    @Test
    void notFoundTest() throws IOException {
        // when
        final URL resource = getClass().getClassLoader().getResource("static/404.html");

        final HttpResponse expected = new HttpResponse(
            new HttpStatusLine(HttpStatusCode.NOT_FOUND),
            new HttpHeaders.Builder()
                .header("Content-Type", "text/html;charset=utf-8")
                .header("Content-Length", "2426")
                .build(),
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        // then
        assertThat(viewResolver.resolve(View.buildByResource("notFound"))).isEqualTo(expected);
    }

    @DisplayName("HttpResponse를 명시한 경우 리소스 탐색하지 않음")
    @Test
    void resolveWithoutResource() {
        final HttpResponse response = new HttpResponse(
            new HttpStatusLine(HttpStatusCode.NOT_FOUND),
            new Builder()
                .header("Content-Type", "text/html;charset=utf-8")
                .header("Content-Length", "2426")
                .build(),
            "test"
        );
        final View view = View.buildByHttpResponse(response);

        assertThat(viewResolver.resolve(view)).isEqualTo(response);
    }

    @DisplayName("View가 명시한 리소스 탐색")
    @Test
    void resolveWithResource() throws IOException {
        final View view = View.buildByResource("/hello.html");
        final URL resource = getClass().getClassLoader().getResource("static/hello.html");

        assertThat(viewResolver.resolve(view)).isEqualTo(
            new HttpResponse(
                new HttpStatusLine(HttpStatusCode.OK),
                new Builder()
                    .header("Content-Type", "text/html;charset=utf-8")
                    .header("Content-Length", "12")
                    .build(),
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
            )
        );
    }
}