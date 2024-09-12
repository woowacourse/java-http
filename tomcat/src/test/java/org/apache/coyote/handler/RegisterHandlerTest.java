package org.apache.coyote.handler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterHandlerTest {

    @Test
    @DisplayName("GET 요청 처리: 회원가입 페이지 반환")
    void handle_GetRequest() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/register.html");
        final String expectedResponseBody = Files.readString(Path.of(resourceURL.getPath()));

        final RequestLine requestLine = new RequestLine("GET", "/register.html", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine);

        final HttpResponse httpResponse = HttpResponse.builder()
                .addLocation("/register.html")
                .foundBuild();
        assertThat(RegisterHandler.getInstance().handle(request)).isEqualTo(httpResponse);
    }

    @Test
    @DisplayName("POST 요청 처리: 유효한 회원가입 정보로 회원가입 성공")
    void handle_PostRequest_WithValidRegistration() {
        final RequestLine requestLine = new RequestLine("POST", "/index.html", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null,
                "account=newuser&email=newuser@example.com&password=password123");

        final HttpResponse httpResponse = HttpResponse.builder()
                .addLocation("/index.html")
                .foundBuild();
        assertThat(RegisterHandler.getInstance().handle(request)).isEqualTo(httpResponse);
    }
}
