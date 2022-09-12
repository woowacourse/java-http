package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.HttpMethodNotAllowedException;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexControllerTest {

    Map<String, String> requestHeaders = new HashMap<>();

    @BeforeEach
    void setUp() {
        requestHeaders.put("Host", "localhost:8080");
        requestHeaders.put("Connection", "keep-alive");
    }

    @DisplayName("doGet 메서드 동작 확인")
    @Test
    void doGet() throws Exception {
        RequestLine startLine = RequestLine.from("GET /index.html HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        IndexController indexController = new IndexController();

        HttpResponse httpResponse = indexController.doGet(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.OK),
                () -> assertThat(httpResponse.getHeaders().getContentType()).isEqualTo("text/html;charset=utf-8")
        );
    }

    @DisplayName("doGet 메서드 동작 확인")
    @Test
    void doPost() throws Exception {
        RequestLine startLine = RequestLine.from("POST /index.html HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);
        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        IndexController indexController = new IndexController();

        assertThatThrownBy(
                () -> indexController.doPost(httpRequest, new HttpResponse())
        ).isInstanceOf(HttpMethodNotAllowedException.class);
    }
}
