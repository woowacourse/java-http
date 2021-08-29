package nextstep.joanne.http.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {
    @Test
    void contentTypeWithCSS() {
        String uri = "/css/a.css";
        HttpRequest httpRequest = new HttpRequest.Builder()
                .uri(uri)
                .build();
        assertThat(httpRequest.contentType()).isEqualTo("text/css");
    }

    @Test
    void contentTypeWithJS() {
        String uri = "/js/a.js";
        HttpRequest httpRequest = new HttpRequest.Builder()
                .uri(uri)
                .build();
        assertThat(httpRequest.contentType()).isEqualTo("application/javascript");
    }
}
