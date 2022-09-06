package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains("Hello world!")
        );
    }

    @Test
    void index() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains(expectedResponseBody)
        );
    }

    @Test
    void css() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains(expectedResponseBody)
        );
    }

    @Test
    void 확장자가_없는_URI로_접근하면_HTML_요청으로_받는다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains(expectedResponseBody)
        );
    }

    @Test
    void 없는_리소스의_주소로_접근하면_404_페이지로_redirect한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /wrong HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /404.html")
        );
    }

    @Test
    void 회원가입_페이지_렌더링() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseBody);
    }

    @Test
    void 로그인_페이지_렌더링() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseBody);
    }

    @Test
    void 회원가입에_성공하면_index_페이지로_redirect한다() {
        String requestBody = "account=ohzzi&password=password&email=ohzzi@woowahan.com";
        int contentLength = requestBody.getBytes().length;
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "",
                requestBody);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html")
        );
    }

    @Test
    void 회원가입_시_중복회원이_있으면_400_페이지로_redirect한다() {
        String requestBody = "account=gugu&password=password&email=hkkang@woowahan.com";
        int contentLength = requestBody.getBytes().length;
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "",
                requestBody);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /400.html")
        );
    }

    @Test
    void 회원가입_시_정보_누락이_있으면_400_페이지로_redirect한다() {
        String requestBody = "query=invalid";
        int contentLength = requestBody.getBytes().length;
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "",
                requestBody);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /400.html")
        );
    }

    @Test
    void 로그인에_성공하면_index_페이지로_redirect한다() {
        // given
        String requestBody = "account=gugu&password=password";
        int contentLength = requestBody.getBytes().length;
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "",
                requestBody);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html")
        );
    }

    @Test
    void 계정_정보가_올바르지_않으면_401_페이지로_redirect한다() {
        // given
        String requestBody = "account=invalid&password=password";
        int contentLength = requestBody.getBytes().length;
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "",
                requestBody);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /401.html")
        );
    }

    @Test
    void 비밀번호가_올바르지_않으면_401_페이지로_redirect한다() {
        // given
        String requestBody = "account=gugu&password=invalid";
        int contentLength = requestBody.getBytes().length;
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "",
                requestBody);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /401.html")
        );
    }

    @Test
    void 쿼리스트링에_account나_password가_들어있지_않으면_401_페이지로_redirect한다() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /401.html")
        );
    }

    @Test
    void JSESSIONID가_없는_요청이_들어오면_새_세션을_만든다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String actual = socket.output();

        assertThat(actual).contains("JSESSIONID=");
    }
}
