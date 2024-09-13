package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 12",
                "Content-Type: text/html;charset=utf-8",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 5564\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인에 성공하면 /index.html로 redirect한다.")
    void should_redirect_to_index_when_login_success() throws IOException, URISyntaxException {
        //given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        LoginController loginController = LoginController.getInstance();
        HttpRequest parsedRequest = parseRawRequest(httpRequest);
        HttpResponse httpResponse = new HttpResponse();

        //when
        loginController.doPost(parsedRequest, httpResponse);

        //then
        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
        assertThat(httpResponse.getHttpResponseHeaders().getValue("Location")).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("로그인에 실패하면 /401.html로 redirect한다.")
    void should_redirect_to_401_when_login_fail() throws IOException, URISyntaxException {
        //given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 31",
                "",
                "account=gugu2&password=password");
        LoginController loginController = LoginController.getInstance();
        HttpRequest parsedRequest = parseRawRequest(httpRequest);
        HttpResponse httpResponse = new HttpResponse();

        //when
        loginController.doPost(parsedRequest, httpResponse);

        //then
        assertThat(parsedRequest.getHttpRequestPath()).isEqualTo("/401.html");
        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("회원가입에 성공하면 /index.html로 redirect한다.")
    void should_redirect_to_index_when_register_success() throws IOException, URISyntaxException {
        //given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 52",
                "",
                "account=gugu2&password=password&email=asdf@gmail.com");
        RegisterController registerController = RegisterController.getInstance();
        HttpRequest parsedRequest = parseRawRequest(httpRequest);
        HttpResponse httpResponse = new HttpResponse();

        //when
        registerController.doPost(parsedRequest, httpResponse);

        //then
        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
        assertThat(httpResponse.getHttpResponseHeaders().getValue("Location")).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("이미 존재하는 계정으로 회원가입을 시도하면 Bad Request를 응답하고 회원가입 페이지로 돌아간다.")
    void should_redirect_to_register_when_login_fail() throws IOException, URISyntaxException {
        //given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 52",
                "",
                "account=gugu&password=password&email=asdf@gmail.com");
        RegisterController registerController = RegisterController.getInstance();
        HttpRequest parsedRequest = parseRawRequest(httpRequest);
        HttpResponse httpResponse = new HttpResponse();

        //when
        registerController.doPost(parsedRequest, httpResponse);

        //then
        assertThat(parsedRequest.getHttpRequestPath()).isEqualTo("/register.html");
        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.BAD_REQUEST);
    }

    private HttpRequest parseRawRequest(String request) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return HttpRequestParser.getInstance().parseRequest(bufferedReader);
    }
}
