package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.MediaType;
import org.apache.coyote.http11.file.FileDetails;
import org.apache.coyote.http11.fixture.HttpRequestFixture;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("redirect 경로를 추가한다.")
    @Test
    void sendRedirect() {
        //given
        String expected = "/index";
        HttpRequest request = HttpRequestFixture.getGetRequest("/login");
        HttpResponse response = new HttpResponse(request);

        //when
        response.sendRedirect(expected);

        //then
        assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, expected);
    }

    @DisplayName("쿠키를 헤더에 추가한다.")
    @Test
    void addCookie() {
        //given
        HttpCookie cookie = HttpCookie.ofJSessionId("sessionId");
        HttpRequest request = HttpRequestFixture.getGetRequest("/login");
        HttpResponse response = new HttpResponse(request);

        //when
        response.addCookie(cookie);

        //then
        assertThat(response.getHeaders()).containsEntry(HttpHeaders.SET_COOKIE, cookie.getCookieToMessage());
    }

    @DisplayName("헤더를 추가한다.")
    @Test
    void addHeader() {
        //given
        String cookieKey = "daon";
        String cookieValue = "back-end";
        HttpRequest request = HttpRequestFixture.getGetRequest("/login");
        HttpResponse response = new HttpResponse(request);

        //when
        response.addHeader(cookieKey, cookieValue);

        //then
        assertThat(response.getHeaders()).containsEntry(cookieKey, cookieValue);
    }

    @DisplayName("URI 경로로 정적 자원을 추가한다.")
    @Test
    void addStaticResource() throws IOException {
        //given
        FileDetails fileDetails = FileDetails.from("/register");
        HttpRequest request = HttpRequestFixture.getGetRequest("/login");
        HttpResponse response = new HttpResponse(request);

        //when
        response.addStaticResource(fileDetails.fileName());

        //then
        URL resource = getClass().getClassLoader().getResource("static" + fileDetails.getFilePath());
        File file = new File(resource.getFile());
        String expected = new String(Files.readAllBytes(file.toPath()));
        String expectedLength = String.valueOf(expected.getBytes().length);
        assertAll(
                () -> assertThat(response.getHeaders())
                        .containsEntry(HttpHeaders.CONTENT_LENGTH, expectedLength),
                () -> assertThat(response.getHeaders())
                        .containsEntry(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML.getValue()),
                () -> assertThat(response.getBody()).isEqualTo(expected)
        );
    }
}
