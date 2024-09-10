package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.InputReader;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    @Test
    @DisplayName("요청 uri에 해당하는 파일을 찾아 결과를 반환한다.")
    void process() throws IOException {
        LoginController controller = new LoginController();
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        InputReader inputReader = new InputReader(inputStream);
        HttpRequest httpRequest = new HttpRequest(inputReader);

        HttpResponse httpResponse = controller.process(httpRequest);

        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = FileReader.read("/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " " + "\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getResponse()).isEqualTo(expected);
    }
}
