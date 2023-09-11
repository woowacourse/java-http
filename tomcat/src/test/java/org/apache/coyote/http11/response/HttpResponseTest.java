package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.common.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpResponse 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void 허용_되지_않은_메서드_응답_테스트() throws IOException {
        // given
        final HttpResponse httpResponse = new HttpResponse();

        // when
        httpResponse.notAllowedMethod();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/405.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertStatusMethod(httpResponse, HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(httpResponse.getBody()).isEqualTo(body);
    }

    @Test
    void 리다이렉트_응답_테스트() {
        // given
        final HttpResponse httpResponse = new HttpResponse();
        final String redirectPath = "/index.html";

        // when
        httpResponse.redirectTo(redirectPath);

        // then
        assertStatusMethod(httpResponse, HttpStatus.FOUND);
        assertThat(httpResponse.getHttpResponseHeaders().getHeaders()).containsEntry("Location", redirectPath);
        assertThat(httpResponse.getBody()).isEmpty();
    }

    @Test
    void 정적_자원_이름으로_응답_테스트() throws IOException {
        // given
        final HttpResponse httpResponse = new HttpResponse();
        final String uri = "/index.html";

        // when
        httpResponse.forwardTo(uri);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertStatusMethod(httpResponse, HttpStatus.OK);
        assertThat(httpResponse.getBody()).isEqualTo(body);
    }

    @Test
    void 정적_자원_이름으로_응답시_존재하지_않는_자원인_경우_404_응답_테스트() throws IOException {
        // given
        final HttpResponse httpResponse = new HttpResponse();
        final String uri = "/NotExistResource.html";

        // when
        httpResponse.forwardTo(uri);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertStatusMethod(httpResponse, HttpStatus.NOT_FOUND);
        assertThat(httpResponse.getBody()).isEqualTo(body);
    }

    @Test
    void 데이터_응답_테스트() {
        // given
        final HttpResponse httpResponse = new HttpResponse();
        final String data = "Hello World!";

        // when
        httpResponse.textPlain(data);

        // then
        assertStatusMethod(httpResponse, HttpStatus.OK);
        assertThat(httpResponse.getBody()).isEqualTo(data);
        assertThat(httpResponse.getHttpResponseHeaders().getHeaders())
                .containsEntry("Content-Type", "text/plain;charset=utf-8");
    }

    private void assertStatusMethod(final HttpResponse httpResponse, final HttpStatus expected) {
        final HttpResponseStatusLine httpResponseStatusLine = httpResponse.getHttpResponseStatusLine();
        assertThat(httpResponseStatusLine.getHttpResponseStatus()).isEqualTo(expected);
    }
}
