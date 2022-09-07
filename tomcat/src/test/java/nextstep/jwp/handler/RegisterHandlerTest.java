package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.HttpRequestGenerator;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterHandlerTest {

    @DisplayName("GET /register 경로로 요청시 200 OK와 함께 Register 페이지를 반환한다.")
    @Test
    void performGetMethod() throws IOException {
        String request = "GET /register HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = RegisterHandler.perform(httpRequest);

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_TYPE)).isEqualTo(
                        ContentType.HTML.getContentType()),
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_LENGTH)).isEqualTo("4319"),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(responseBody)
        );
    }

    @DisplayName("Post /register 경로로 요청시 302 Found와 함께 index 페이지를 반환한다.")
    @Test
    void performPostMethod() throws IOException {
        String request = "POST /register HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 80\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Accept: */*\n"
                + "\n"
                + "account=gugu&password=password&email=hkkang%40woowahan.com\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = RegisterHandler.perform(httpRequest);

        assertAll(
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.LOCATION)).isEqualTo("/index.html")
        );
    }
}
