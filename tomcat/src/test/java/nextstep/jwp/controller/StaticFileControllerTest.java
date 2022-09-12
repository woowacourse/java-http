package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticFileControllerTest {

    Map<String, String> requestHeaders = new HashMap<>();

    @BeforeEach
    void setUp() {
        requestHeaders.put("Host", "localhost:8080");
        requestHeaders.put("Connection", "keep-alive");
    }

    @DisplayName("html 파일 로딩 확인 -> index.html")
    @Test
    void get_index_html() throws Exception {
        RequestLine startLine = RequestLine.from("GET /index.html HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        StaticFileController staticFileController = new StaticFileController();

        HttpResponse httpResponse = staticFileController.doGet(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.OK),
                () -> assertThat(httpResponse.getHeaders().getContentType()).isEqualTo("text/html;charset=utf-8")
        );
    }

    @DisplayName("css 파일 로딩 확인 -> text.css")
    @Test
    void get_styles_css() throws Exception {
        RequestLine startLine = RequestLine.from("GET /css/styles.css HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        StaticFileController staticFileController = new StaticFileController();

        HttpResponse httpResponse = staticFileController.doGet(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.OK),
                () -> assertThat(httpResponse.getHeaders().getContentType()).isEqualTo("text/css;charset=utf-8")
        );
    }

    @DisplayName("js 파일 로딩 확인 -> scripts.js")
    @Test
    void get_script_js() throws Exception {
        RequestLine startLine = RequestLine.from("GET /js/scripts.js HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        StaticFileController staticFileController = new StaticFileController();

        HttpResponse httpResponse = staticFileController.doGet(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.OK),
                () -> assertThat(httpResponse.getHeaders().getContentType()).isEqualTo("text/javascript;charset=utf-8")
        );
    }
}
