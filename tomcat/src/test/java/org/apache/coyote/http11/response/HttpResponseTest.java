package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpResponseTest {

    @Test
    void 응답_라인_생성_테스트() {
        // given
        HttpResponse httpResponse = new HttpResponse();

        // when
        String expected = "HTTP/1.1 200 OK \r\n\r\n";

        // then
        assertThat(httpResponse.format()).isEqualTo(expected);
    }

    @Test
    void 베이스_헤더_생성_테스트() {
        // given
        HttpResponse httpResponse = new HttpResponse();

        // when
        httpResponse.addBaseHeader("text/html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html \r\n\r\n";

        // then
        assertThat(httpResponse.format()).isEqualTo(expected);
    }

    @Test
    void 헤더_생성_테스트() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.redirect("/index.html");

        // when
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n\r\n";

        // then
        assertThat(httpResponse.format()).isEqualTo(expected);
    }

    @Test
    void 쿠키_추가_테스트() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.redirect("/index.html");

        // when
        httpResponse.setCookie("cookie-i-made");
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Set-Cookie: JSESSIONID=cookie-i-made \r\n\r\n";

        // then
        assertThat(httpResponse.format()).isEqualTo(expected);
    }

    @Test
    void 문자열_바디_생성_테스트() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.redirect("/index.html");
        httpResponse.addBody("Hello World!");

        // when
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Content-Length: 12 \r\n\r\n" +
                "Hello World!";

        // then
        assertThat(httpResponse.format()).isEqualTo(expected);
    }

    @Test
    void 파일_바디_생성_테스트() throws IOException {
        // given
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.redirect("/index.html");
        String responseBody = FileReader.read("/index.html");

        // when
        httpResponse.addBody(responseBody);

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Content-Length: 5564 \r\n\r\n" +
                body;

        // then
        assertThat(httpResponse.format()).isEqualTo(expected);
    }
}
