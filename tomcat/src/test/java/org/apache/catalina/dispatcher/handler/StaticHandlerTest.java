package org.apache.catalina.dispatcher.handler;

import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixture.get;
import static support.HttpRequestFixture.request;

class StaticHandlerTest {

    private final StaticHandler staticHandler = new StaticHandler();

    @Test
    @DisplayName("static 하위에 있는 리소스를 GET으로 요청하면 처리한다.")
    void supportsExistingResource() throws IOException {
        assertThat(staticHandler.supports(get("/index.html"))).isTrue();
    }

    @Test
    @DisplayName("하위 디렉터리에 있는 리소스도 처리한다.")
    void supportsNestedResource() throws IOException {
        assertThat(staticHandler.supports(get("/css/styles.css"))).isTrue();
        assertThat(staticHandler.supports(get("/assets/img/error-404-monochrome.svg"))).isTrue();
    }

    @Test
    @DisplayName("쿼리 스트링이 있어도 경로로 판단한다.")
    void supportsWithQueryString() throws IOException {
        assertThat(staticHandler.supports(get("/index.html?page=1"))).isTrue();
    }

    @Test
    @DisplayName("루트 경로는 index.html로 처리한다.")
    void supportsWelcomeFile() throws IOException {
        assertThat(staticHandler.supports(get("/"))).isTrue();
        assertThat(staticHandler.handle(get("/"), new HttpResponse())).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("존재하지 않는 리소스는 처리하지 않는다.")
    void notSupportsMissingResource() throws IOException {
        assertThat(staticHandler.supports(get("/not-found.html"))).isFalse();
    }

    @Test
    @DisplayName("디렉터리 경로는 처리하지 않는다.")
    void notSupportsDirectory() throws IOException {
        assertThat(staticHandler.supports(get("/css"))).isFalse();
    }

    @Test
    @DisplayName("컨트롤러가 처리할 경로는 처리하지 않는다.")
    void notSupportsEndpoint() throws IOException {
        assertThat(staticHandler.supports(get("/login"))).isFalse();
    }

    @Test
    @DisplayName("GET이 아닌 요청은 처리하지 않는다.")
    void notSupportsNonGet() throws IOException {
        assertThat(staticHandler.supports(request("POST", "/index.html"))).isFalse();
    }

    @Test
    @DisplayName("static 밖으로 나가는 경로는 처리하지 않는다.")
    void notSupportsPathTraversal() throws IOException {
        assertThat(staticHandler.supports(get("/../static/index.html"))).isFalse();
    }

    @Test
    @DisplayName("요청 경로를 static을 뺀 뷰 이름으로 반환한다.")
    void handle() throws IOException {
        assertThat(staticHandler.handle(get("/css/styles.css"), new HttpResponse()))
                .isEqualTo("/css/styles.css");
    }

}
