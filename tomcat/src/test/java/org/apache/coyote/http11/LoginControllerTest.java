package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    @Test
    @DisplayName("요청 uri에 해당하는 파일을 찾아 결과를 반환한다.")
    void process() throws IOException {
        LoginController controller = new LoginController();

        HttpResponse httpResponse = controller.process("/login");

        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = FileReader.read("static/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " " + "\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getResponse()).isEqualTo(expected);
    }
}
