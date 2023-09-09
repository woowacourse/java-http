package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class ResourceHandlerMapperTest {

    @Test
    void 디폴트_경로로_요청시_DefaultResourceHandler를_반환한다() {
        final HttpRequest request = HttpRequest.of("GET / HTTP/1.1 ", new LinkedHashMap<>(), "");
        final Controller resourceHandler = ResourceHandlerMapper.findHandler(request);

        assertThat(resourceHandler).isInstanceOf(DefaultResourceHandler.class);
    }

    @Test
    void 기타_경로로_요청시_StaticResourceHandler를_반환한다() {
        final HttpRequest request = HttpRequest.of("GET /index.html HTTP/1.1 ", new LinkedHashMap<>(), "");
        final Controller resourceHandler = ResourceHandlerMapper.findHandler(request);

        assertThat(resourceHandler).isInstanceOf(StaticResourceHandler.class);
    }
}
