package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.FrontController;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.HttpRequestGenerator;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexHandlerTest {

    @DisplayName("GET /login 경로로 요청시 200 OK와 함께 Hello world가 담긴 HttpResponse를 반환한다.")
    @Test
    void nonStaticFileBasicRequest() throws IOException {
        FrontController frontController = new FrontController();
        String request = "GET / HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n";
        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = frontController.performRequest(httpRequest);

        String expectedResponseBody = "Hello world!";

        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_TYPE)).isEqualTo(
                        ContentType.HTML.getContentType()),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_LENGTH)).isEqualTo(
                        String.valueOf(expectedResponseBody.getBytes().length)),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(expectedResponseBody)
        );
    }
}
