package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpRequestTest {

    @DisplayName("inputStream이 HttpRequest로 잘 파싱되는지 확인")
    @ParameterizedTest
    @MethodSource
    void inputStreamParseTest(String req,
                              HttpMethod expectedMethod,
                              String expectedFilePath,
                              Map<String, List<String>> expectedHeaders) {
        //given
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(req.getBytes());
        //when
        HttpRequest request = HttpRequest.of(byteArrayInputStream);
        //then
        System.out.println(request);
        assertThat(request.filepath()).isEqualTo(expectedFilePath);
        assertThat(request.method()).isEqualTo(expectedMethod);
        expectedHeaders.forEach((headerKey, headerValues) ->
            assertThat(
                request.header(headerKey)
                    .list())
                .containsExactlyInAnyOrderElementsOf(headerValues));
    }

    private static Stream<Arguments> inputStreamParseTest() {
        return Stream.of(
            Arguments.of(
                String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    ""),
                HttpMethod.GET,
                "/index.html",
                Map.of("Host", List.of("localhost:8080"), "Connection", List.of("keep-alive"))
            ),
            Arguments.of(
                String.join("\r\n",
                    "POST /foo/foo.html HTTP/1.1 ",
                    "Host: 123.41.2.5:55723 ",
                    "Content-type: text/html ",
                    "",
                    ""),
                HttpMethod.POST,
                "/foo/foo.html",
                Map.of("Host", List.of("123.41.2.5:55723"), "Content-type", List.of("text/html"))
            ),
            Arguments.of(
                String.join("\r\n",
                    "GET / HTTP/1.1 ",
                    "Host: 0.0.0.0 ",
                    "set-cookie: a:b,b:c,c:d ",
                    "",
                    ""),
                HttpMethod.GET,
                "/",
                Map.of("Host", List.of("0.0.0.0"), "set-cookie", List.of("a:b", "b:c", "c:d"))
            )
        );
    }
}