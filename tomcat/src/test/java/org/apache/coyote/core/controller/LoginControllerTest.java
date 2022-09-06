package org.apache.coyote.core.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("./login url 접근 시, 유저의 비밀번호가 일치하면 예외가 발생하지 않는다.")
    @Test
    void login() throws IOException {
        // given
        String httpRequestProtocol = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password= &email=hkkang%40woowahan.com ");

        InputStream inputStream = new ByteArrayInputStream(httpRequestProtocol.getBytes());
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequest httpRequest = new HttpRequest(bufferReader);
        HttpResponse httpResponse = new HttpResponse();

        // when & then
        LoginController controller = new LoginController();
        controller.service(httpRequest, httpResponse);
    }
}
