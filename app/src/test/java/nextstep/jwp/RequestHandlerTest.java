package nextstep.jwp;

import nextstep.jwp.httpmessage.ContentType;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestHandlerTest {

    @Test
    void url_입력하지_않을시_기본_메세지_반환한다() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        String expected = toHttp200Response("Hello world!", ContentType.HTML);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 슬래시를_Path로_둘_때_기본_메세지를_반환한다() {
        // given
        final String requestUri = "/";
        final String httpRequest = toHttpGetRequest(requestUri);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        String expected = toHttp200Response("Hello world!", ContentType.HTML);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/login.html", "/register.html"})
    void 정적_html_파일을_반환한다(String requestUri) throws IOException {
        // given
        final String httpRequest = toHttpGetRequest(requestUri);
        final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String expectResponseBody = Files.readString(new File(resource.getFile()).toPath());
        String expected = toHttp200Response(expectResponseBody, ContentType.HTML);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    static private Stream<Arguments> Post_요청_실패에_대해_응답한다() {
        return Stream.of(
                Arguments.of("/login", "account=&password=password", "/401.html"),
                Arguments.of("/login", "account=gugu&password=passwor", "/401.html"),
                Arguments.of("/login", "account=ggu&password=password", "/401.html"),
                Arguments.of("/register", "account=gugu&password=passwor&email=", "/401.html"),
                Arguments.of("/register", "account=gugu&password=&email=test@test.com", "/401.html"),
                Arguments.of("/register", "account=&password=&email=test@test.com", "/401.html"),
                Arguments.of("/ishavePage", "", "/404.html"),
                Arguments.of("/ishavePage", "account", "/404.html"),
                Arguments.of("/is.html", "account", "/404.html")
        );
    }

    @ParameterizedTest
    @MethodSource
    void Post_요청_실패에_대해_응답한다(String requestUri, String requestBody, String redirectUrl) {
        // given
        final String httpRequest = toHttpPostRequest(requestUri, requestBody);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        String expected = toHttp302Response(redirectUrl);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 유저_로그인_테스트() {
        //given
        final String requestUri = "/login";
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = toHttpPostRequest(requestUri, requestBody);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        String expected = toHttp302ResponseWithSession("/index.html");
        //when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).startsWith(expected);
    }

    @Test
    void 유저_가입_테스트() {
        // given
        final String requestUri = "/register";
        final String requestBody = "account=gumgum&password=password2&email=ggump%40woowahan.com";
        final String httpRequest = toHttpPostRequest(requestUri, requestBody);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        String expected = toHttp302Response("/index.html");
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> JS_및_CSS_요청_테스트() {
        return Stream.of(
                Arguments.of("/assets/chart-area.js", ContentType.JAVASCRIPT),
                Arguments.of("/assets/chart-bar.js", ContentType.JAVASCRIPT),
                Arguments.of("/assets/chart-pie.js", ContentType.JAVASCRIPT),
                Arguments.of("/css/styles.css", ContentType.CSS)
        );
    }

    @ParameterizedTest
    @MethodSource
    void JS_및_CSS_요청_테스트(String requestUri, ContentType contentType) throws IOException {
        //give
        final String httpRequest = toHttpGetRequest(requestUri);
        final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String expectResponseBody = Files.readString(new File(resource.getFile()).toPath());
        String expected = toHttp200Response(expectResponseBody, contentType);
        //when
        requestHandler.run();
        final String actual = socket.output();
        //then
        assertThat(actual).isEqualTo(expected);
    }

    public static String toHttpGetRequest(String requestUri) {
        return String.join("\r\n",
                "GET " + requestUri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    public static String toHttpPostRequest(String requestUri, String requestBody) {
        return String.join("\r\n",
                "POST " + requestUri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody);
    }

    private String toHttp302Response(String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080" + redirectUrl + " ",
                "",
                "");
    }

    private String toHttp302ResponseWithSession(String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080" + redirectUrl + " ",
                "Set-Cookie: JESSIONID=");
    }

    private String toHttp200Response(String expectResponseBody, ContentType contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + " ",
                "Content-Length: " + expectResponseBody.getBytes().length + " ",
                "",
                expectResponseBody);
    }
}
