package nextstep.jwp.framework.manager;

import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.request.details.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicWebManagerTest {

    private final DynamicWebManager dynamicWebManager = new DynamicWebManager();

    @DisplayName("application 패키지 내의 controller를 매핑하고, 처리가 가능한지 검사할 수 있다.")
    @Test
    void canHandle() {

        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.GET, "/"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.GET, "/login"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.POST, "/login"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.GET, "/register"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.POST, "/register"))).isTrue();
    }

    @DisplayName("알맞은 처리를 Controller에게 위임하고 결과값을 받아올 수 있다.")
    @Test
    void handle() {
        final String indexResult = dynamicWebManager.handle(HttpRequest.of(HttpMethod.GET, "/"));
        final String loginResult = dynamicWebManager.handle(HttpRequest.of(HttpMethod.GET, "/login"));

        assertThat(indexResult).isEqualTo("/index.html");
        assertThat(loginResult).isEqualTo("/login.html");
    }
}
