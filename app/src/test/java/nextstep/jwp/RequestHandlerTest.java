package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

public class RequestHandlerTest {

    private final Assembler assembler = new Assembler();

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

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
    void notFound() throws IOException {
        // given
        final String httpRequest = get("/invalid.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertResponse(socket.output(), HttpStatus.NOT_FOUND, body);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = get("/index.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertResponse(socket.output(), HttpStatus.OK, body);
    }

    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest = get("/login");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertResponse(socket.output(), HttpStatus.OK, body);
    }

    @Test
    void login() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = post("/login", requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: index.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginWithUnauthorized() throws IOException {
        // given
        final String requestBody = "account=invalidAccount&password=password";
        final String httpRequest = post("/login", requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertResponse(socket.output(), HttpStatus.UNAUTHORIZED, body);
    }


    @Test
    void register() throws IOException {
        // given
        final String httpRequest = get("/register");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertResponse(socket.output(), HttpStatus.OK, body);
    }

    @Test
    void registerPost() {
        final String account = "corgi";
        final String password = "password";
        final String email = "hkkang%40woowahan.com";

        // given
        final String requestBody = "account=" + account + "&password=" + password + "&email=" + email;
        final String httpRequest = post("/register", requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: index.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
        InMemoryUserRepository.delete(new User(account, password, email));
    }

    public static void assertResponse(String output, HttpStatus httpStatus, String body) {
        String expected = "HTTP/1.1 " + httpStatus.code() + " " + httpStatus.status() + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(output).isEqualTo(expected);
    }

    private static String get(String url) {
        return String.join("\r\n",
                "GET " + url + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    private static String post(String url, String requestBody) {
        return String.join("\r\n",
                "POST " + url + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                requestBody,
                "");
    }
}
