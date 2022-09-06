package org.apache.coyote.core.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("회원가입을 할 때, account, password, email 중 공백인 값이 있으면 예외를 발생한다.")
    @Test
    void register_null_password() throws IOException {
        // given
        String httpRequestProtocol = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html;charset=utf-8",
                "Connection: keep-alive ",
                "Content-Length: 32",
                "",
                "account=gugu&password=invalidpassword");

        InputStream inputStream = new ByteArrayInputStream(httpRequestProtocol.getBytes());
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequest httpRequest = new HttpRequest(bufferReader);
        HttpResponse httpResponse = new HttpResponse();

        // when & then
        RegisterController registerController = new RegisterController();
        assertThatThrownBy(() -> registerController.service(httpRequest, httpResponse))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
