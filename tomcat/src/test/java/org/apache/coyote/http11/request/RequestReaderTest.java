package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestReaderTest {

    @Test
    void 리퀘스트_파싱_테스트() throws IOException {
        // given
        String request =
                "GET /index.html HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Accept: */*\r\n" +
                        "\r\n";

        // when
        try (BufferedReader br = new BufferedReader(new StringReader(request))) {
            RequestReader requestReader = new RequestReader(br);

            requestReader.read();

            // then
            assertSoftly(softly -> {
                softly.assertThat(requestReader.getMethod()).isEqualTo("GET");
                softly.assertThat(requestReader.getRequestUri()).isEqualTo("/index.html");
                softly.assertThat(requestReader.getProtocol()).isEqualTo("HTTP/1.1");
            });
        }
    }

    @Test
    void 세션_없음_테스트() {
        // given
        String request =
                "GET /index.html HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Cookie: yummy=1234\r\n" +
                        "Accept: */*\r\n" +
                        "\r\n";

        // when
        try (BufferedReader br = new BufferedReader(new StringReader(request))) {
            RequestReader requestReader = new RequestReader(br);

            requestReader.read();

            // then
            assertThat(requestReader.hasSessionId()).isFalse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void 세션_있을떄_쿠키_확인_테스트() {
        // given
        String request =
                "GET /index.html HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Accept: */*\r\n" +
                        "Cookie: JSESSIONID=1234\r\n" +
                        "\r\n";

        // when
        try (BufferedReader br = new BufferedReader(new StringReader(request))) {
            RequestReader requestReader = new RequestReader(br);

            requestReader.read();

            // then
            assertThat(requestReader.hasSessionId()).isTrue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
