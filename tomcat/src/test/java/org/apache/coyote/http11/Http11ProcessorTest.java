package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        RequestMappings requestMappings = makeRequestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMappings);

        MockedStatic<Http11Cookie> http11CookieMockedStatic = Mockito.mockStatic(Http11Cookie.class);
        http11CookieMockedStatic.when(Http11Cookie::sessionCookie).thenReturn(new Http11Cookie("JSESSIONID", "test"));

        // when
        processor.process(socket);
        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length:5564\r\n" +
                "Content-Type:text/html;charset=utf-8\r\n" +
                "Set-Cookie: JSESSIONID=test\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(Path.of(resource.toURI())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    private RequestMappings makeRequestMapping() {
        StaticResourceController staticResourceController = new StaticResourceController();
        RequestMappings requestMappings = new RequestMappings(staticResourceController);
        requestMappings.putController(staticResourceController, "/*.js", "/*.css", "/", "/index", "/index.html");
        LoginController loginController = new LoginController();
        requestMappings.putController(loginController, "/login", "/login.html");
        requestMappings.putController(new RegisterController(), "/register", "/register.html");
        return requestMappings;
    }
}
