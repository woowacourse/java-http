package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void 요청의_헤더와_파라미터는_외부_변경으로부터_보호된다() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("host", "localhost");
        final Map<String, String> params = new HashMap<>();
        params.put("page", "1");

        final HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethod.GET)
                .path("/index")
                .version("HTTP/1.1")
                .headers(headers)
                .params(params)
                .build();
        headers.clear();
        params.clear();

        assertThat(request.headers()).containsEntry("host", "localhost");
        assertThat(request.params()).containsEntry("page", "1");
        assertThatThrownBy(() -> request.headers().put("name", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> request.params().put("name", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
