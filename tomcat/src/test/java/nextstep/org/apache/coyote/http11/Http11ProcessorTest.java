package nextstep.org.apache.coyote.http11;

import nextstep.jwp.db.InMemorySession;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import support.StubSocket;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private String getRequest(String method , String path, String body){
        int length = body.getBytes().length;
        if(length == 0){
            return String.join("\r\n",
                    method + " " + path +" "+"HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    body);
        }
        return String.join("\r\n",
                method + " " + path +" "+"HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body);
    }

    private String getResponseHeader(int code, String status , String responseBody){
        return "HTTP/1.1 " + code + " "+ status + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length;
    }
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = getResponseHeader(200,"OK","Hello world!");

        assertThat(socket.output()).contains(expected);
        assertThat(socket.output()).contains("Hello world!");
    }
    @DisplayName("페이지 테스트")
    @Nested
    class PageTest{
        @DisplayName("index 페이지에 조회할 수 있다")
        @Test
        void indexPageTest () throws IOException {
        // given
        final String httpRequest = getRequest("GET","/index.html","");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expectedHeader = getResponseHeader(200,"OK",responseBody);

        assertThat(socket.output()).contains(expectedHeader);
        assertThat(socket.output()).contains(responseBody);
        }

        @DisplayName("login 페이지에 조회할 수 있다.")
        @Test
        void loginPageTest () throws IOException {
        // given
        final String httpRequest = getRequest("GET","/login.html","");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expectedHeader = getResponseHeader(200,"OK",responseBody);

        assertThat(socket.output()).contains(expectedHeader);
        assertThat(socket.output()).contains(responseBody);
        }

        @DisplayName("login 상태에서 페이지에 조회시 메인 화면으로 리다이렉트 된다.")
        @Test
        void loginPageTest_redirect () throws IOException {
            // given
            final String jSessionId = InMemorySession.login(new User("","","")).toString();
            final String httpRequest =  String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Cookie: JSESSIONID="+ jSessionId,
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            var expectedHeader = getResponseHeader(200,"OK",responseBody);

            assertThat(socket.output()).contains(expectedHeader);
            assertThat(socket.output()).contains(responseBody);
        }

        @DisplayName("register 페이지에 조회할 수 있다.")
        @Test
        void registerPageTest () throws IOException {
        // given
        final String httpRequest = getRequest("GET","/register.html","");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expectedHeader = getResponseHeader(200,"OK",responseBody);


        assertThat(socket.output()).contains(expectedHeader);
        assertThat(socket.output()).contains(responseBody);
        }
    }

    @DisplayName("로그인 테스트")
    @Nested
    class LoginTest{
        @DisplayName("로그인을 할 수 있다")
        @Test
        void login() throws IOException {
            // given
            final String body = "account=gugu&password=password";
            final String httpRequest = getRequest("POST", "/login", body);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = getResponseHeader(302,"Found",responseBody);
            final String expectedLocation = "location : index.html";
            assertThat(socket.output()).contains(expected);
            assertThat(socket.output()).contains(responseBody);
            assertThat(socket.output()).contains(expectedLocation);
        }

        @DisplayName("잘못된 비번은 로그인에 실패한다")
        @Test
        void login_Fail() throws IOException {
            // given
            final String body = "account=gugu&password=passwor";
            final String httpRequest = getRequest("POST", "/login", body);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = getResponseHeader(401,"Unauthorized",responseBody);
            final String expectedLocation = "location : 401.html";
            assertThat(socket.output()).contains(expected);
            assertThat(socket.output()).contains(responseBody);
            assertThat(socket.output()).contains(expectedLocation);
        }
    }

    @DisplayName("회원가입 테스트")
    @Nested
    class Register{
        @DisplayName("회원가입을 할 수 있다")
        @Test
        void register() throws IOException {
            // given
            final String body = "account=ako&password=akofighting&email=123@123";
            final String httpRequest = getRequest("POST", "/register", body);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = getResponseHeader(302,"Found",responseBody);
            final String expectedLocation = "location : index.html";
            assertThat(socket.output()).contains(expected);
            assertThat(socket.output()).contains(responseBody);
            assertThat(socket.output()).contains(expectedLocation);
        }
    }
}
