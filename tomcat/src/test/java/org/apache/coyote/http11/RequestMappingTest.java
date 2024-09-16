package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.Controller;
import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticController;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.coyote.http11.message.parser.HttpRequestParser;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @DisplayName("/ -> IndexController 반환")
    @Test
    void getIndexController() throws IOException {
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));

        Controller actual = RequestMapping.getController(httpRequest);

        assertThat(actual).isInstanceOf(IndexController.class);
    }

    @DisplayName("/login -> LoginController 반환")
    @Test
    void getLoginController() throws IOException {
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));

        Controller actual = RequestMapping.getController(httpRequest);

        assertThat(actual).isInstanceOf(LoginController.class);
    }

    @DisplayName("/register -> RegisterController 반환")
    @Test
    void getRegisterController() throws IOException {
        String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: 80 ",
                "",
                "account=gugu&password=password&email=hkkang@woowahan.com ");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));

        Controller actual = RequestMapping.getController(httpRequest);

        assertThat(actual).isInstanceOf(RegisterController.class);
    }

    @DisplayName("일치하는 path가 없으면 -> StaticController 반환")
    @Test
    void getStaticController() throws IOException {
        String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));

        Controller actual = RequestMapping.getController(httpRequest);

        assertThat(actual).isInstanceOf(StaticController.class);
    }
}
