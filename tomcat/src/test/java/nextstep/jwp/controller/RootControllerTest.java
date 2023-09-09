package nextstep.jwp.controller;

import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.common.MediaType.TEXT_HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RootControllerTest {

    private final RootController rootController = new RootController();

    @DisplayName("RootController는 정해진 request를 핸들링할 수 있다.")
    @Test
    void canHandle() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        // when
        boolean actual = rootController.canHandle(httpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("RootController는 정해진 request 외의 요청을 핸들링할 수 없다.")
    @ParameterizedTest(name = "method: {0}, target: {1}")
    @CsvSource({
            "POST, /",
            "GET, /login"
    })
    void canHandle_fail(String method, String target) {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from(method, target, "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        // when
        boolean actual = rootController.canHandle(httpRequest);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("GET 요청 시, 해당되는 자원을 반환한다.")
    @Test
    void doGet() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(CONTENT_TYPE.getName(), TEXT_HTML.stringifyWithUtf());

        // when
        rootController.doGet(httpRequest, httpResponse);

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.OK);
                    softly.assertThat(httpResponse).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                    softly.assertThat(httpResponse).extracting("body")
                            .isEqualTo("Hello world!");
                }
        );
    }
}
