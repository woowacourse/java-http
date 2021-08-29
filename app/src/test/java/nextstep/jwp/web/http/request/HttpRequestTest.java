package nextstep.jwp.web.http.request;

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
                              Map<String, String> queryParams,
                              Map<String, List<String>> expectedHeaders) {
        //given
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(req.getBytes());
        //when
        HttpRequest request = HttpRequest.of(byteArrayInputStream);
        //then
        assertThat(request.url()).isEqualTo(expectedFilePath);
        assertThat(request.method()).isEqualTo(expectedMethod);
        assertThat(request.queryParam().map()).containsExactlyInAnyOrderEntriesOf(
            queryParams
        );
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
                    "GET /index HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    ""),
                HttpMethod.GET,
                "/index",
                Map.of(),
                Map.of("Host", List.of("localhost:8080"), "Connection", List.of("keep-alive"))
            ),
            Arguments.of(
                String.join("\r\n",
                    "POST /foo/foo HTTP/1.1 ",
                    "Host: 123.41.2.5:55723 ",
                    "Content-type: text/html ",
                    "",
                    ""),
                HttpMethod.POST,
                "/foo/foo",
                Map.of(),
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
                Map.of(),
                Map.of("Host", List.of("0.0.0.0"), "set-cookie", List.of("a:b", "b:c", "c:d"))
            ),
            Arguments.of(
                String.join("\r\n",
                    "GET /index?query1=value1&query2=value2 HTTP/1.1 ",
                    "Host: 0.0.0.0 ",
                    "set-cookie: a:b,b:c,c:d ",
                    "",
                    ""),
                HttpMethod.GET,
                "/index",
                Map.of("query1", "value1", "query2", "value2"),
                Map.of("Host", List.of("0.0.0.0"), "set-cookie", List.of("a:b", "b:c", "c:d"))
            ),
            Arguments.of(
                String.join("\r\n",
                    "GET /index?user=gugu&password=password HTTP/1.1 ",
                    "Host: 0.0.0.0 ",
                    "set-cookie: a:b , c:d ",
                    "",
                    ""),
                HttpMethod.GET,
                "/index",
                Map.of("user", "gugu", "password", "password"),
                Map.of("Host", List.of("0.0.0.0"), "set-cookie", List.of("a:b", "c:d"))
            )
        );
    }
}