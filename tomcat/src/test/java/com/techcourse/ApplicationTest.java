package com.techcourse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.servlet.HelloWorldServlet;
import com.techcourse.servlet.HomeServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterServlet;
import com.techcourse.servlet.StaticResourceServlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.servlet.ServletContainer;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class ApplicationTest {

    @BeforeAll
    static void setUpServlets() {
        ServletContainer servletContainer = ServletContainer.getInstance();
        servletContainer.add("/", new HelloWorldServlet());
        servletContainer.add("/index.html", new HomeServlet());
        servletContainer.add("/login", new LoginServlet());
        servletContainer.add("/register", new RegisterServlet());
        servletContainer.setFallBackServlet(new StaticResourceServlet());
    }

    @Nested
    class 루트_경로_케이스 {
        @Test
        void get() {
            // given
            final var socket = new StubSocket();
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                    () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                    () -> assertThat(output).contains("Content-Length: 12"),
                    () -> assertThat(output).endsWith("Hello world!")
            );
        }
    }

    @Nested
    class index_경로_케이스 {

        @Test
        void get() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);

            // static/index.html 읽기
            URL resource = getClass().getClassLoader().getResource("static/index.html");
            byte[] bodyBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            String body = new String(bodyBytes);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                    () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                    () -> assertThat(output).contains("Content-Length: " + bodyBytes.length),
                    () -> assertThat(output).endsWith(body)
            );
        }
    }

    @Nested
    class login_경로_케이스 {

        @Test
        void get() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);

            // static/login.html 읽기
            URL resource = getClass().getClassLoader().getResource("static/login.html");
            byte[] bodyBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            String body = new String(bodyBytes);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                    () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                    () -> assertThat(output).contains("Content-Length: " + bodyBytes.length),
                    () -> assertThat(output).endsWith(body)
            );
        }

        @Test
        void post_로그인_성공() {
            // given
            String account = "gugu";
            String password = "password";
            String requestBody = "account=" + account + "&password=" + password;

            String httpRequest =
                    "POST /login HTTP/1.1\r\n" +
                            "Host: localhost:8080\r\n" +
                            "Connection: keep-alive\r\n" +
                            "Content-Length: " + requestBody.length() + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded\r\n" +
                            "Accept: */*\r\n" +
                            "\r\n" +
                            requestBody;

            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 303 See Other"),
                    () -> assertThat(output).contains("Location: /index.html")
            );
        }

        @Test
        void post_로그인_인증_실패() {
            // given
            String account = "gugu";
            String password = "otherPassword";
            String requestBody = "account=" + account + "&password=" + password;

            String httpRequest =
                    "POST /login HTTP/1.1\r\n" +
                            "Host: localhost:8080\r\n" +
                            "Connection: keep-alive\r\n" +
                            "Content-Length: " + requestBody.length() + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded\r\n" +
                            "Accept: */*\r\n" +
                            "\r\n" +
                            requestBody;

            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 303 See Other"),
                    () -> assertThat(output).contains("Location: /401.html")
            );
        }
    }

    @Nested
    class 잘못된_경로_케이스 {

        @Test
        void 존재하지_않는_경로_GET() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /non-exists HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);

            // static/404.html 읽기
            URL resource = getClass().getClassLoader().getResource("static/404.html");
            byte[] bodyBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            String body = new String(bodyBytes);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 404 Not Found"),
                    () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                    () -> assertThat(output).contains("Content-Length: " + bodyBytes.length),
                    () -> assertThat(output).endsWith(body)
            );
        }
    }

    @Nested
    class register_경로_케이스 {

        @Test
        void get() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);

            // static/register.html 읽기
            URL resource = getClass().getClassLoader().getResource("static/register.html");
            byte[] bodyBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            String body = new String(bodyBytes);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                    () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                    () -> assertThat(output).contains("Content-Length: " + bodyBytes.length),
                    () -> assertThat(output).endsWith(body)
            );
        }

        @Test
        void post_회원가입_성공() {
            // given
            String account = "ed";
            String password = "1234";
            String email = "ed@gmail.com";
            String requestBody = "account=" + account + "&password=" + password + "&email=" + email;

            String httpRequest =
                    "POST /register HTTP/1.1\r\n" +
                            "Host: localhost:8080\r\n" +
                            "Connection: keep-alive\r\n" +
                            "Content-Length: " + requestBody.length() + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded\r\n" +
                            "Accept: */*\r\n" +
                            "\r\n" +
                            requestBody;

            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, ServletContainer.getInstance());

            // when
            processor.process(socket);
            String output = socket.output();

            // then
            assertAll(
                    () -> assertThat(output).startsWith("HTTP/1.1 303 See Other"),
                    () -> assertThat(output).contains("Location: /index.html")
            );
        }
    }

}
