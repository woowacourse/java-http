package org.apache.coyote.http11;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void mapsRegisterPathToRegisterController() {
        final var controller = requestMapping.getController(request("/register"));

        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @Test
    void mapsLoginPathToLoginController() {
        final var controller = requestMapping.getController(request("/login"));

        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void mapsUnknownPathToStaticResourceController() {
        final var controller = requestMapping.getController(request("/index.html"));

        assertThat(controller).isInstanceOf(StaticResourceController.class);
    }

    private HttpRequest request(final String path) {
        return new HttpRequest("GET", path, "HTTP/1.1", Map.of());
    }
}
