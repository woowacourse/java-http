package nextstep.jwp.domain.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    private Map<String, String> headerMap = new LinkedHashMap<>();

    @BeforeEach
    void setUp() {
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", "12");
    }

    @DisplayName("바이트배열로 변환한다.")
    @Test
    void testGetBytes() {
        //given
        HttpResponse response = new HttpResponse(HttpStatus.OK, headerMap, "Hello world!");
        byte[] expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!").getBytes(StandardCharsets.UTF_8);
        //when
        byte[] actual = response.getBytes();
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest(name = "파일명이 파라미터로 전달되는 경우 HtttpResponse Body에 담아 반환한다.")
    @MethodSource
    void getBody(String uri, int contentLength) throws IOException {
        //given
        HttpResponse response = new HttpResponse();
        //when
        HttpResponse result = response.respond(uri);
        //then
        int length = result.getBody().getBytes(StandardCharsets.UTF_8).length;
        assertThat(length).isEqualTo(contentLength);
    }

    static Stream<Arguments> getBody() {
        return Stream.of(
                Arguments.of("/index.html", 5564),
                Arguments.of("/login.html", 3797),
                Arguments.of("/register.html", 4319),
                Arguments.of("/400.html", 2347));
    }

    @ParameterizedTest(name = "파일명이 파라미터로 전달되면 해당 파일을 찾아 응답한다.")
    @MethodSource
    void testRespond(String uri, HttpStatus statusCode) throws IOException {
        //given
        HttpResponse response = new HttpResponse();
        //when
        HttpResponse result = response.respond(uri);
        //then
        assertThat(result.getHttpStatus()).isEqualTo(statusCode);
    }

    static Stream<Arguments> testRespond() {
        return Stream.of(
                Arguments.of("/index.html", HttpStatus.OK),
                Arguments.of("/login.html", HttpStatus.OK),
                Arguments.of("/register.html", HttpStatus.OK));
    }
}
