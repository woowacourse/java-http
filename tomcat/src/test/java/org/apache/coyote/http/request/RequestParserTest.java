package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Test;
import org.apache.coyote.http.HttpMethod;

class RequestParserTest {

    private final RequestParser parser = RequestParser.getInstance();

    @Test
    void 요청을_파싱한다() {
        // given
        String request = """
                GET /users?team=ddangkong HTTP/1.1
                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
                Accept-Encoding: gzip, deflate, br, zstd
                Accept-Language: ko,en-US;q=0.9,en;q=0.8
                Connection: keep-alive
                Cache-Control: no-cache
                Host: localhost:8080
                Content-Length: 31
                Content-Type: application/x-www-form-urlencoded
                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36
                
                account=prin&password=1q2w3e4r!
                """;

        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // when
            Request actual = parser.parse(bufferedReader);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.getHttpMethod()).isEqualTo(HttpMethod.GET);
                softly.assertThat(actual.getPath()).isEqualTo("/users");
                softly.assertThat(actual.getQueryParamValue("team")).isEqualTo("ddangkong");
                softly.assertThat(actual.getBodyValue("account")).isEqualTo("prin");
                softly.assertThat(actual.getBodyValue("password")).isEqualTo("1q2w3e4r!");
            });
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void RequestLine이_없으면_예외가_발생한다() {
        // given
        String request = "";

        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // when & then
            assertThatThrownBy(() -> parser.parse(bufferedReader))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Request line은 필수입니다.");
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void RequestHeader_포맷이_올바르지_않을_경우_예외가_발생한다() {
        // given
        String request = """
                GET /index.html HTTP/1.1
                Connection - keep-alive
                Host: localhost:8080
                """;

        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // when & then
            assertThatThrownBy(() -> parser.parse(bufferedReader))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("올바르지 않은 Request header 포맷입니다. header: Connection - keep-alive");
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void ContentLength_헤더가_없을_경우_빈_RequestBody를_반환한다() {
        // given
        String request = """
                GET /index.html HTTP/1.1
                Connection: keep-alive
                Host: localhost:8080
                """;

        try (InputStream inputStream = new ByteArrayInputStream(request.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // when
            Request actual = parser.parse(bufferedReader);

            // when & then
            assertThatThrownBy(() -> actual.getBodyValue("any"))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Request body가 비어있습니다.");
        } catch (Exception e) {
            fail(e);
        }
    }
}
