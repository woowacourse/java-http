package nextstep.jwp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.BeanFactory;
import nextstep.jwp.httpserver.RequestHandler;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RequestHandler 테스트")
class RequestHandlerTest {

    @Test
    @DisplayName("GET / 요청시 index.html 반환")
    void run() {
        // given
        BeanFactory.init();
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5670"
        );
    }

    @Test
    @DisplayName("GET /index.html 요청 시 index.html 반환")
    void index() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        BeanFactory.init();
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5670"
        );
    }

    @Test
    @DisplayName("GET /login 요청 시 login.html 반환")
    void loginPage() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        BeanFactory.init();
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 3863 "
        );
    }

    @Test
    @DisplayName("POST /login 요청 시 index.html 반환")
    void login() {
        // given
        String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                body);

        BeanFactory.init();
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 3863 "
        );
    }

    @Test
    @DisplayName("POST /login 요청 시 잘못된 비밀번호 입력")
    void loginWrongPassword() {
        // given
        String body = "account=gugu&password=password1";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                body);

        BeanFactory.init();
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found ",
                "Location: /401.html"
        );
    }

    @Test
    @DisplayName("GET /register 요청 시 register.html 반환")
    void registerPage() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        BeanFactory.init();
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 4391 "
        );
    }

    @Test
    @DisplayName("POST /register 요청 시 index.html 반환")
    void register() {
        // given
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                body);

        BeanFactory.init();
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 3863 "
        );
    }

    @Test
    @DisplayName("css 정적 리소스 관리")
    void staticResource() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive ",
                "");

        BeanFactory.init();
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: 223257"
        );
    }
}
