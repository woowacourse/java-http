package jakarta.controller;

import jakarta.http.Header;
import jakarta.http.HttpBody;
import jakarta.http.HttpHeaderKey;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpSessionWrapper;
import jakarta.http.HttpVersion;
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
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);

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
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);

        assertThatThrownBy(() -> staticResourceController.service(request, response))
                .isInstanceOf(NoSuchElementException.class);
    }

    private HttpRequest createHttpRequest(String requestLine) {
        return HttpRequest.createHttpRequest(requestLine,
                Header.empty(),
                mock(HttpBody.class),
                HttpVersion.HTTP_1_1,
                mock(HttpSessionWrapper.class)
        );
    }
}
