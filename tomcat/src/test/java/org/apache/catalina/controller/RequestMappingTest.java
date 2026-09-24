package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final Controller login = (request, response) -> response.redirect("/index.html");
    private final Controller fallback = (request, response) -> response.status(200, "OK");

    @Test
    void selectsExactPathWithoutQuery() throws IOException {
        RequestMapping mapping = new RequestMapping(Map.of("/login", login), fallback);

        assertThat(mapping.getController(request("/login?next=index"))).isSameAs(login);
    }

    @Test
    void returnsFallbackForUnmappedPath() throws IOException {
        RequestMapping mapping = new RequestMapping(Map.of("/login", login), fallback);

        assertThat(mapping.getController(request("/unknown"))).isSameAs(fallback);
        assertThat(mapping.getController(request("/login/extra"))).isSameAs(fallback);
    }

    @Test
    void copiesMappingsAtConstruction() throws IOException {
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/login", login);
        RequestMapping mapping = new RequestMapping(controllers, fallback);

        controllers.put("/login", fallback);
        controllers.put("/later", login);

        assertThat(mapping.getController(request("/login"))).isSameAs(login);
        assertThat(mapping.getController(request("/later"))).isSameAs(fallback);
    }

    private HttpRequest request(String target) throws IOException {
        String message = "GET " + target + " HTTP/1.1\r\n\r\n";
        return HttpRequest.readFrom(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
    }
}
