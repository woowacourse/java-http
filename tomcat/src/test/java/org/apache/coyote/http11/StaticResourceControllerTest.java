package org.apache.coyote.http11;

import org.apache.catalina.Manager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class StaticResourceControllerTest {

    @Test
    @DisplayName("정적 데이터를 처리한다.")
    void handleResource() throws Exception {
        StaticResourceController staticResourceController = new StaticResourceController();
        HttpRequest request = createHttpRequest("GET /sample.txt HTTP/1.1");
        HttpResponse response = HttpResponse.createHttp11Response();

        staticResourceController.service(request, response);

        String body = response.getBody();
        Header responseHeader = response.getHeader();
        Optional<String> contentType = responseHeader.get(HttpHeaderKey.CONTENT_TYPE);

        assertThat(body).isEqualTo("sample");
        assertThat(contentType).hasValue("text/plain;charset=utf-8");
    }

    @Test
    @DisplayName("정적 데이터를 찾을 수 없다면 예외를 발생한다.")
    void cantHandle() {
        StaticResourceController staticResourceController = new StaticResourceController();
        HttpRequest request = createHttpRequest("GET /none.txt HTTP/1.1");
        HttpResponse response = HttpResponse.createHttp11Response();

        assertThatThrownBy(() -> staticResourceController.service(request, response))
                .isInstanceOf(NoSuchElementException.class);
    }

    private HttpRequest createHttpRequest(String requestLine) {
        return HttpRequest.createHttp11Request(requestLine,
                Header.empty(),
                mock(HttpBody.class),
                mock(Manager.class)
        );
    }
}
