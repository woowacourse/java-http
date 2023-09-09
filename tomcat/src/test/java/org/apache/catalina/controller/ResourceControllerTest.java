package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.common.MediaType.TEXT_HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ResourceControllerTest {

    private final ResourceController resourceController = new ResourceController();
    @DisplayName("ResourceController는 GET 메서드를 핸들링할 수 있다.")
    @Test
    void canHandle() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/index.html", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        // when
        boolean actual = resourceController.canHandle(httpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("ResourceController는 GET 이외의 메서드를 핸들링할 수 없다.")
    @Test
    void canHandle_fail() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("POST", "/index.html", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        // when
        boolean actual = resourceController.canHandle(httpRequest);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("GET 요청 시, 정적 리소스를 반환한다.")
    @Test
    void doGet() throws IOException {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/index.html", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(CONTENT_TYPE.getName(), TEXT_HTML.stringifyWithUtf());

        // when
        resourceController.doGet(httpRequest, httpResponse);

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.OK);
                    softly.assertThat(httpResponse).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                }
        );
    }

    @DisplayName("존재하지 않는 리소스에 대한 GET 요청 시, HttpException이 발생한다.")
    @Test
    void doGet_invalidResource() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/invalid.html", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        HttpResponse httpResponse = HttpResponse.init();

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThatThrownBy(() -> resourceController.doGet(httpRequest, httpResponse))
                            .isInstanceOf(HttpException.class);
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.NOT_FOUND);
                }
        );
    }

    @DisplayName("지원하지 않는 content-type의 resource에 대한 GET 요청 시, HttpException이 발생한다.")
    @Test
    void doGet_invalidContentType() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/invalid.sql", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        HttpResponse httpResponse = HttpResponse.init();

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThatThrownBy(() -> resourceController.doGet(httpRequest, httpResponse))
                            .isInstanceOf(HttpException.class);
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.NOT_FOUND);
                }
        );
    }
}
