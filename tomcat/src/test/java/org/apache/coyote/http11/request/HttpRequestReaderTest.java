package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpRequestMethod 테스트")
class HttpRequestReaderTest {

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
        final HttpRequest httpRequest = HttpRequestReader.readHttpRequest(stubSocket.getInputStream());

        // then
        assertSoftly(softAssertions -> {
                    assertThat(httpRequest.getHttpStartLine().getHttpRequestMethod()).isEqualTo(HttpRequestMethod.GET);
                    assertThat(httpRequest.getHttpStartLine().getRequestURI()).isEqualTo("/index.html");
                    assertThat(httpRequest.getHttpStartLine().getHttpVersion()).isEqualTo("HTTP/1.1");
                    assertThat(httpRequest.getHttpRequestHeaders().get("Connection")).isEqualTo("keep-alive");
                    assertThat(httpRequest.getHttpRequestHeaders().get("Host")).isEqualTo("localhost:8080");
                    assertThat(httpRequest.getHttpRequestHeaders().get("Cache-Control")).isEqualTo("max-age=0");
                }
        );
    }

}
