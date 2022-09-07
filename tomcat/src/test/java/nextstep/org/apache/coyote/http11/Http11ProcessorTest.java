package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.Session;
import org.apache.coyote.util.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("기본 경로를 요청할 경우 hello World를 출력한다.")
    void process() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = 응답한다("200 OK", "Hello world!");

        assertThat(expected).contains("200 OK");
        assertThat(expected).contains("Hello world!");
    }

    @Test
    @DisplayName("index 페이지를 요청할 경우 index.html을 응답한다.")
    void index() throws IOException {
        // given
        String httpRequest = 요청한다("GET", "/index.html");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = 응답한다("200 OK", new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(expected).contains("200 OK");
    }

    @Test
    @DisplayName("css 페이지를 요청할 경우 해당 css 파일을 응답한다.")
    void css() throws IOException {
        // given
        String httpRequest = 요청한다("GET", "/css/styles.css");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String response = socket.output();

        assertThat(response).contains("200 OK");
        assertThat(response).contains("text/css;charset=utf-8");
    }

    @Test
    @DisplayName("js 페이지를 요청할 경우 해당 js 파일을 응답한다.")
    void js() throws IOException {
        // given
        String httpRequest = 요청한다("GET", "/js/scripts.js");
        var socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String response = socket.output();

        assertThat(response).contains("200 OK");
        assertThat(response).contains("text/js;charset=utf-8");
    }

    @Nested
    @DisplayName("로그인 요청시")
    class Login {

        @Test
        @DisplayName("GET 요청시 로그인 페이지로 이동한다.")
        void get_login() throws IOException {
            // given
            String request = 요청한다("GET", "/login.html");
            var socket = new StubSocket(request);
            Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            assertThat(response).contains("200 OK");
        }

        @Test
        @DisplayName("GET 요청시 세션이 존재할 경우 index 페이지로 이동한다.")
        void get_login_with_session() throws IOException {
            // given
            String id = UUID.randomUUID().toString();
            Session session = new Session(id);
            session.setAttribute("user", new User("gugu", "password", "gugu@email.com"));
            SessionManager.add(session);

            String request = "GET /login.html HTTP/1.1\n"
                    + "Host: localhost:8080\n"
                    + "Content-Length: " + "account=gugu&password=password" + "\n"
                    + "Connection: keep-alive\n"
                    + "Cookie: JSESSIONID= " + id + ";\n"
                    + "Accept: */*\n"
                    + "\n"
                    + "account=gugu&password=password";
            StubSocket socket = new StubSocket(request);
            Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            assertThat(response).contains("302 Found");
            assertThat(response).contains("Location: /index.html");
        }

        @Test
        @DisplayName("POST 요청시 계정과 비밀번호가 일치하면 index 페이지로 리다이렉트 된다.")
        void post_login() throws IOException {
            // given
            String request = 요청한다("POST", "/login", "account=gugu&password=password");
            var socket = new StubSocket(request);
            Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            assertThat(response).contains("302 Found");
            assertThat(response).contains("Location: /index.html");
            assertThat(response).contains("Set-Cookie: JSESSIONID");
        }

        @Test
        @DisplayName("POST 요청시 계정과 비밀번호가 일치하지 않으면 401 페이지로 리다이렉트 된다.")
        void post_login_fail() throws IOException {
            // given
            String request = 요청한다("POST", "/login", "account=gugu&password=kkkk");
            var socket = new StubSocket(request);
            Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            assertThat(response).contains("401 Unauthorized");
            assertThat(response).contains("Location: /401.html");
        }
    }

    @Nested
    @DisplayName("회원가입 요청시")
    class Register {

        @Test
        @DisplayName("GET 요청시 회원가입 페이지로 이동한다.")
        void get_register() throws IOException {
            // given
            String request = 요청한다("GET", "/register.html");
            var socket = new StubSocket(request);
            Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            assertThat(response).contains("200 OK");
        }

        @Test
        @DisplayName("POST 요청으로 회원 가입을 요청한다.")
        void post_register() throws IOException {
            // given
            String request = 요청한다("POST", "/register", "account=hoho&email=hoho@email.com&password=password");
            var socket = new StubSocket(request);
            Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            assertThat(response).contains("302 Found");
            assertThat(response).contains("Location: /index.html");
            assertThat(response).contains("Set-Cookie: JSESSIONID");
        }

        @Test
        @DisplayName("POST 요청으로 회원 가입시 존재하는 회원일 경우 404 페이지로 리다이렉트 된다.")
        void post_register_fail() throws IOException {
            // given
            String request = 요청한다("POST", "/register", "account=eve&email=hoho@email.com&password=password");
            StubSocket socket = new StubSocket(request);
            Http11Processor processor1 = new Http11Processor(socket);
            processor1.process(socket);

            // when
            String register = 요청한다("POST", "/register", "account=eve&email=hoho@email.com&password=password");
            socket = new StubSocket(register);
            Http11Processor processor2 = new Http11Processor(socket);
            processor2.process(socket);

            // then
            String response = socket.output();

            System.out.println(response);
            assertThat(response).contains("404 Not Found");
            assertThat(response).contains("Location: /404.html");
        }
    }

    private String 요청한다(String method, String path) {
        return String.join("\r\n",
                method + " " + path + " " + "HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
    }

    private String 요청한다(String method, String path, String body) {
        return method + " " + path + " " + "HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Content-Length: " + body.getBytes().length + "\n"
                + "Connection: keep-alive\n"
                + "Accept: */*\n"
                + "\n"
                + body;
    }

    private String 응답한다(String status, String body) {
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String 응답한다(String status, String body, ContentType contentType) {
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType.getValue() + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
