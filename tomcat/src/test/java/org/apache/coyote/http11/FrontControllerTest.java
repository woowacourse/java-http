package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;

import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.HttpRequestGenerator;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    private final FrontController frontController = new FrontController();

    @DisplayName("처리할 수 없는 요청이 올 경우 경우 Not Found 응답을 한다.")
    @Test
    void responseBadRequestWhenRequestThatCannotBePerform() throws IOException {
        String request = "GET /wrongUrl HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n";
        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = frontController.performRequest(httpRequest);

        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
    }
}
