package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpMethod;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpRequestParameter;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.data.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {
    private final RegisterController registerController = RegisterController.getInstance();

    @Test
    @DisplayName("[POST] 회원가입한다.")
    void register() {
        // given
        String requestBody = "account=mia&password=password";
        HttpRequestParameter requestParameter = new HttpRequestParameter(
                Map.of("account", "mia", "password", "password", "email", "mia@google.com"));
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/register", HttpVersion.HTTP_1_1, requestBody,
                new ContentType(MediaType.URLENC, null), requestBody.length(), requestParameter, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        registerController.doPost(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(httpResponse.getRedirectUrl()).isEqualTo("/index.html");
            assertThat(InMemoryUserRepository.findByAccount("mia")).isNotNull();
        });
    }

    @Test
    @DisplayName("[POST] 회원가입 시 필수 입력값을 모두 입력하지 않는다.")
    void registerWithInvalidRequest() {
        // given
        String requestBody = "account=gugu&password=password";
        HttpRequestParameter requestParameter = new HttpRequestParameter(
                Map.of("account", "mia", "password", "password"));
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/register", HttpVersion.HTTP_1_1, requestBody,
                new ContentType(MediaType.URLENC, null), requestBody.length(), requestParameter, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        registerController.doPost(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(httpResponse.getRedirectUrl()).isEqualTo("/400.html");
        });
    }

    @Test
    @DisplayName("[GET] 회원가입 페이지를 조회한다.")
    void getRegisterPage() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/register", HttpVersion.HTTP_1_1, null,
                new ContentType(MediaType.HTML, "charset=utf-8"), 0, null, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        registerController.doGet(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
            assertThat(httpResponse.getContentType().getMediaType()).isEqualTo(MediaType.HTML);
            assertThat(httpResponse.getResponseBody()).isNotEmpty();
        });
    }
}
