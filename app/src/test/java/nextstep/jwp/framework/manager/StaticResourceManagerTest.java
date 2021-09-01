package nextstep.jwp.framework.manager;

import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.request.details.HttpMethod;
import nextstep.jwp.framework.http.response.HttpResponse;
import nextstep.jwp.framework.http.response.details.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StaticResourceManagerTest {

    private final StaticResourceManager staticResourceManager = new StaticResourceManager();

    @DisplayName("해당 요청이 정적자원에 대한 요청인지 확인한다.")
    @Test
    void canHandle() {
        assertThat(staticResourceManager.canHandle(HttpRequest.of(HttpMethod.GET, "/test.html"))).isTrue();
        assertThat(staticResourceManager.canHandle(HttpRequest.of(HttpMethod.GET, "/css/test.css"))).isTrue();

        assertThat(staticResourceManager.canHandle(HttpRequest.of(HttpMethod.GET, "/not_in_test.html"))).isFalse();
        assertThat(staticResourceManager.canHandle(HttpRequest.of(HttpMethod.GET, "/css/not_in_test.css"))).isFalse();
    }

    @DisplayName("해당 요청을 처리하고 HttpResponse를 반환한다.")
    @Test
    void handle() {
        final HttpResponse httpResponse = staticResourceManager.handle(HttpRequest.of(HttpMethod.GET, "/test.html"));
        assertThat(httpResponse.getProtocolVersion()).isEqualTo(ProtocolVersion.defaultVersion());
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }
}
