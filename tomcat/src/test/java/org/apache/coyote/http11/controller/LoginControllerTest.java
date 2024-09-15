package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestBuilder;

class LoginControllerTest extends AbstractControllerTest {


    @DisplayName("GET /login 요청 시, login.html을 응답한다.")
    @Test
    void getLoginWithNoParameter() throws IOException {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/login")
                .build();
        LoginController controller = new LoginController();
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        controller.doGet(request, response);
        String actual = outputStream.toString();

        // then
        assertThat(actual).contains(expected);
    }
}