package org.apache.catalina.controller;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.requestline.RequestLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestMappingTest {
    private final SessionManager sessionManager = new SessionManager();
    private final Controller login = new AbstractController() {};
    private final Controller fallback = new AbstractController() {};
    private final RequestMapping mapping = new RequestMapping(Map.of("/login", login), fallback);

    @Test
    void 매핑된_경로는_해당_컨트롤러를_반환한다() {
        assertThat(mapping.getController(request("/login"))).isSameAs(login);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/css/styles.css", "/unknown"})
    void 매핑되지_않은_경로는_기본_컨트롤러를_반환한다(final String path) {
        assertThat(mapping.getController(request(path))).isSameAs(fallback);
    }

    @Test
    void 쿼리는_매핑에_영향을_주지_않는다() {
        assertThat(mapping.getController(request("/login?account=gugu"))).isSameAs(login);
    }

    @Test
    void 인코딩된_경로도_디코딩된_값으로_매핑한다() {
        assertThat(mapping.getController(request("/%6Cogin"))).isSameAs(login);   // %6C = 'l'
    }

    private HttpRequest request(final String target) {
        return HttpRequest.of(
                RequestLine.from("GET " + target + " HTTP/1.1"),
                RequestHeaders.from(List.of("Host: localhost")),
                RequestBody.empty(),
                sessionManager
        );
    }
}