package nextstep.jwp;

import nextstep.jwp.response.CharlieHttpResponse;
import nextstep.jwp.response.HttpStatusCode;
import nextstep.jwp.web.FrontController;
import nextstep.jwp.web.RequestHandler;
import nextstep.jwp.web.WebApplicationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    private final FrontController frontController = WebApplicationConfig.frontController();

    @DisplayName("/index path로 index.html 리소스에 대한 응답을 한다.")
    @Test
    void index() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        CharlieHttpResponse httpResponse = CharlieHttpResponse.createResponse("index.html", HttpStatusCode.OK);
        String expectedResponse = httpResponse.toHttpResponseMessage();

        // then
        assertThat(socket.output()).isEqualTo(expectedResponse);
    }

    @DisplayName("/index.html path로 index.html 리소스에 대한 응답을 한다.")
    @Test
    void indexDotHTML() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        CharlieHttpResponse httpResponse = CharlieHttpResponse.createResponse("/index.html", HttpStatusCode.OK);
        String expectedResponse = httpResponse.toHttpResponseMessage();

        // then
        assertThat(socket.output()).isEqualTo(expectedResponse);
    }

    @DisplayName("/login path로 GET 요청을 보내서 login.html 리소스에 대한 응답을 한다.")
    @Test
    void loginPage() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        CharlieHttpResponse httpResponse = CharlieHttpResponse.createResponse("login.html", HttpStatusCode.OK);
        String expectedResponse = httpResponse.toHttpResponseMessage();

        // then
        assertThat(socket.output()).isEqualTo(expectedResponse);
    }

    @DisplayName("/login path로 POST 요청을 보내서 로그인 유저 정보가 유효하면 /index.html 로 리다이렉트 한다.")
    @Test
    void loginPost() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        // then
        CharlieHttpResponse httpResponse = CharlieHttpResponse.redirectResponse("/index.html");
        String expectedResponse = httpResponse.toHttpResponseMessage();

        assertThat(socket.output()).isEqualTo(expectedResponse);
    }

    @DisplayName("/login path로 POST 요청을 보내서 로그인 유저 정보가 유효하지 않으면 /401.html 로 리다이렉트 한다.")
    @Test
    void loginWithInvalidUser() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=1234");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        // then
        CharlieHttpResponse httpResponse = CharlieHttpResponse.redirectResponse("/401.html");
        String expectedResponse = httpResponse.toHttpResponseMessage();

        assertThat(socket.output()).isEqualTo(expectedResponse);
    }

    @DisplayName("/register path로 GET 요청을 보내서 register.html 리소스에 대한 응답을 한다.")
    @Test
    void registerPage() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        // then
        CharlieHttpResponse httpResponse = CharlieHttpResponse.createResponse("register.html", HttpStatusCode.OK);
        String expectedResponse = httpResponse.toHttpResponseMessage();

        assertThat(socket.output()).isEqualTo(expectedResponse);
    }

    @DisplayName("/register path로 POST 요청을 보내서 로그인 유저 정보가 유효하면 /index.html 로 리다이렉트 한다.")
    @Test
    void register() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu1&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        // then
        CharlieHttpResponse httpResponse = CharlieHttpResponse.redirectResponse("/index.html");
        String expectedResponse = httpResponse.toHttpResponseMessage();

        assertThat(socket.output()).isEqualTo(expectedResponse);
    }

    @DisplayName("/register path로 POST 요청을 보내서 회원가입에 실패하면 /409.html 로 리다이렉트 한다.")
    @Test
    void registerWithDuplicateUser() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 48 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=1234&email=gugu%40test.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, frontController);

        // when
        requestHandler.run();

        // then
        CharlieHttpResponse httpResponse = CharlieHttpResponse.redirectResponse("/409.html");
        String expectedResponse = httpResponse.toHttpResponseMessage();

        assertThat(socket.output()).isEqualTo(expectedResponse);
    }
}
