package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HandlerMappingTest {

    @ParameterizedTest
    @ValueSource(strings = {"/index", "/login", "/register"})
    void 등록된_경로의_컨트롤러를_찾는다(final String path) {
        // given & when
        final HttpRequest request = createRequest(path);

        // then
        assertThat(new HandlerMapping().getController(request)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/css/styles.css", "/not-found"})
    void 등록되지_않은_경로에는_컨트롤러가_없다(final String path) {
        // given & when
        final HttpRequest request = createRequest(path);

        // then
        assertThat(new HandlerMapping().getController(request)).isEmpty();
    }

    private HttpRequest createRequest(final String path) {
        return new HttpRequest(
            new RequestLine(HttpMethod.GET, path, HttpVersion.VERSION_11),
            HttpHeaders.from(List.of()),
            "");
    }
}
