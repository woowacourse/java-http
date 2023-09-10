package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.response.Header.LOCATION;
import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ResponseTest {

    @Test
    void 응답_라인_생성_테스트() {
        // given
        Response response = new Response();

        // when
        response.addResponseLine("HTTP/1.1", OK);
        String expected = "HTTP/1.1 200 OK \r\n\r\n";

        // then
        assertThat(response.format()).isEqualTo(expected);
    }

    @Test
    void 베이스_헤더_생성_테스트() {
        // given
        Response response = new Response();
        response.addResponseLine("HTTP/1.1", OK);

        // when
        response.addBaseHeader("text/html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html \r\n\r\n";

        // then
        assertThat(response.format()).isEqualTo(expected);
    }

    @Test
    void 헤더_생성_테스트() {
        // given
        Response response = new Response();
        response.addResponseLine("HTTP/1.1", FOUND);

        // when
        response.addHeader(LOCATION, "/index.html");
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n\r\n";

        // then
        assertThat(response.format()).isEqualTo(expected);
    }

    @Test
    void 쿠키_추가_테스트() {
        // given
        Response response = new Response();
        response.addResponseLine("HTTP/1.1", FOUND);
        response.addHeader(LOCATION, "/index.html");

        // when
        response.setCookie("cookie-i-made");
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Set-Cookie: JSESSIONID=cookie-i-made \r\n\r\n";

        // then
        assertThat(response.format()).isEqualTo(expected);
    }

    @Test
    void 문자열_바디_생성_테스트() {
        // given
        Response response = new Response();
        response.addResponseLine("HTTP/1.1", FOUND);
        response.addHeader(LOCATION, "/index.html");

        // when
        response.createBodyByText("Hello World!");
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Content-Length: 12 \r\n\r\n" +
                "Hello World!";

        // then
        assertThat(response.format()).isEqualTo(expected);
    }

    @Test
    void 파일_바디_생성_테스트() throws IOException {
        // given
        Response response = new Response();
        response.addResponseLine("HTTP/1.1", FOUND);
        response.addHeader(LOCATION, "/index.html");


        // when
        response.createBodyByFile("/index.html");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Content-Length: 5564 \r\n\r\n" +
                body;

        // then
        assertThat(response.format()).isEqualTo(expected);
    }
}
