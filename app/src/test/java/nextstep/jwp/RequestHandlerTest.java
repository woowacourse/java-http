package nextstep.jwp;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= request("GET", "/index.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest= request("GET", "/login");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: "+body.getBytes().length+" \r\n" +
                "\r\n"+
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginWithAccountAndPassword() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: "+ requestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                requestBody,
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: index.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() throws IOException {
        // given
        final String httpRequest= request("GET", "/register");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: "+body.getBytes().length+" \r\n" +
                "\r\n"+
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void registerPost() {
        // given
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: "+ requestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                requestBody,
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: index.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    private static String request(String method, String url){
        return String.join("\r\n",
                method+" "+url+" HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }
}
