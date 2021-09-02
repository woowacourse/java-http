package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    void notFound() {
        // given
        final String httpRequest = get("/invalid.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        String responseStatusLine = socket.output().split("\r\n")[0];
        assertThat(responseStatusLine).isEqualTo("HTTP/1.1 404 Not Found ");
    }

    @Test
    void index() {
        // given
        final String httpRequest = get("/index.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        String responseStatusLine = socket.output().split("\r\n")[0];
        assertThat(responseStatusLine).isEqualTo("HTTP/1.1 200 OK ");
    }

    @Test
    void loginPage() {
        // given
        final String httpRequest = get("/login");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        String responseStatusLine = socket.output().split("\r\n")[0];
        assertThat(responseStatusLine).isEqualTo("HTTP/1.1 200 OK ");
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
        String responseStatusLine = socket.output().split("\r\n")[0];
        assertThat(responseStatusLine).isEqualTo("HTTP/1.1 302 Found ");
    }

    @Test
    void loginWithUnauthorized() {
        // given
        final String requestBody = "account=invalidAccount&password=password";
        final String httpRequest = post("/login", requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        String responseStatusLine = socket.output().split("\r\n")[0];
        assertThat(responseStatusLine).isEqualTo("HTTP/1.1 401 Unauthorized ");
    }

    @Test
    void register() {
        // given
        final String httpRequest = get("/register");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, assembler.dispatcher());

        // when
        requestHandler.run();

        // then
        String responseStatusLine = socket.output().split("\r\n")[0];
        assertThat(responseStatusLine).isEqualTo("HTTP/1.1 200 OK ");
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
        String responseStatusLine = socket.output().split("\r\n")[0];
        assertThat(responseStatusLine).isEqualTo("HTTP/1.1 302 Found ");
        InMemoryUserRepository.delete(new User(account, password, email));
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
