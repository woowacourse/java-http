package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.URI;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MainControllerTest {

    @DisplayName("[GET] (/) 메인 페이지")
    @Test
    void get() {
        MainController mainController = new MainController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());
        HttpResponse httpResponse = mainController.doGet(
                new HttpRequest(requestLine, requestHeaders, RequestBody.ofEmpty()));

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo("Hello World!"),
                () -> assertThat(httpResponse.getContentType()).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8)
        );
    }

    @DisplayName("[POST] (/) NotFound")
    @Test
    void postNotFound() {
        MainController mainController = new MainController();

        String protocol = "HTTP/1.1";
        URI uri = new URI("/");
        RequestLine requestLine = new RequestLine(Method.POST, uri, protocol);
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());
        HttpResponse httpResponse = mainController.doPost(
                new HttpRequest(requestLine, requestHeaders, RequestBody.ofEmpty()));

        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
