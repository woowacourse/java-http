package com.techcourse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        servletContainer.add("/", new com.techcourse.servlet.HelloWorldServlet());
        servletContainer.add("/index.html", new com.techcourse.servlet.HomeServlet());
        servletContainer.add("/login", new com.techcourse.servlet.LoginServlet());
        servletContainer.setFallBackServlet(new com.techcourse.servlet.StaticResourceServlet());
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

}
