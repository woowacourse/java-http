package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.parser.HttpRequestMessageReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpMethod 테스트")
class HttpRequestMessageReaderTest {

    @Test
    void 요청스트림을_읽어_HttpRequest를_생성한다() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cache-Control: max-age=0 ",
                "sec-ch-ua: \"Not)A;Brand\";v=\"24\", \"Chromium\";v=\"116\" ",
                "sec-ch-ua-mobile: ?0 ",
                "sec-ch-ua-platform: \"macOS\" ",
                "DNT: 1 ",
                "Upgrade-Insecure-Requests: 1 ",
                "",
                "");
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);

        // when
        final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(stubSocket.getInputStream());

        // then
        assertSoftly(softAssertions -> {
                    assertThat(httpRequest.getHttpStartLine().getHttpRequestMethod()).isEqualTo(HttpMethod.GET);
                    assertThat(httpRequest.getHttpStartLine().getRequestURI()).isEqualTo("/index.html");
                    assertThat(httpRequest.getHttpStartLine().getHttpVersion()).isEqualTo("HTTP/1.1");
                    assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
                    assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080");
                    assertThat(httpRequest.getHeader("Cache-Control")).isEqualTo("max-age=0");
                }
        );
    }

    @Test
    void 잘못된_HTTP_요청_메세지_시작라인인_경우_예외_발생() {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET /index.html wrongSize HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);

        // when & then
        assertThatThrownBy(() -> HttpRequestMessageReader.readHttpRequest(stubSocket.getInputStream()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 라인의 토큰은 3개여야 합니다.");
    }

    @Test
    void 쿼리파라미터와_URI를_분리하여_저장한다() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET /index.html?name=royce&password=p1234 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final StubSocket stubSocket = new StubSocket(httpRequestMessage);

        // when
        final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(stubSocket.getInputStream());

        // then
        assertSoftly(softAssertions -> {
            assertThat(httpRequest.getParam("name")).isEqualTo("royce");
            assertThat(httpRequest.getParam("password")).isEqualTo("p1234");
        });
    }
}
