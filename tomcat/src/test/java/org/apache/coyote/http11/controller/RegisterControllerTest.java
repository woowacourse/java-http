package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StaticResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterControllerTest {
    private final Controller registerController = new RegisterController();
    private HttpRequest getRequest;
    private HttpRequest postRequest;

    @BeforeEach
    void setUp() throws IOException {
        // given
        final String getRawRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Content-Type: text/html ",
                "",
                "");
        final StringReader getReader = new StringReader(getRawRequest);
        getRequest = HttpRequest.from(new BufferedReader(getReader));

        final String requestBody = "account=hubk&password=hubkpassword";
        final String postRawRequest = String.join("\r\n",
                "POST /register.html HTTP/1.1 ",
                "Content-Type: text/html ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);
        final StringReader postReader = new StringReader(postRawRequest);
        postRequest = HttpRequest.from(new BufferedReader(postReader));
    }

    @Test
    @DisplayName("지원하지 않는 HttpMethod는 해당 컨트롤러가 처리할 수 없다.")
    void handleExceptionTest() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "DELETE /index.html HTTP/1.1 ",
                "Content-Type: text/html ",
                "",
                "");
        final StringReader stringReader = new StringReader(rawRequest);
        getRequest = HttpRequest.from(new BufferedReader(stringReader));

        //when, then
        assertThatThrownBy(() -> registerController.service(getRequest, HttpResponse.create()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("register.html을 응답할 수 있다.")
    void requestTest() throws Exception {
        //when
        final HttpResponse response = HttpResponse.create();
        registerController.service(getRequest, response);

        //then
        final StaticResource staticResource = StaticResource.from("/register.html");
        final byte[] content = staticResource.getContent();
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.length + " ",
                "",
                new String(content));
        assertThat(response.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("새로운 유저를 등록하면 index.html로 리다이렉트한다.")
    void registerUserAndRedirect() throws Exception {
        //given
        final String requestBody = "account=hubk&password=hubkpassword";
        final String postRawRequest = String.join("\r\n",
                "POST /register.html HTTP/1.1 ",
                "Content-Type: text/html ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);
        final StringReader postReader = new StringReader(postRawRequest);
        final HttpRequest postRequest = HttpRequest.from(new BufferedReader(postReader));

        //when
        final HttpResponse response = HttpResponse.create();
        registerController.service(postRequest, response);

        //then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "",
                "");
        assertThat(response.toString()).isEqualTo(expected);
    }
}
