package com.techcourse.controller;

import static org.apache.coyote.http.MediaType.TEXT_HTML;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;
import org.apache.coyote.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Test
    @DisplayName("Handle root")
    void root() throws IOException {
        // given
        final String path = "/";
        final String input = String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var request = new HttpRequest(input);

        final var sut = FrontController.getInstance();

        // when
        final var actual = sut.handle(request);

        // then
        final var body = new HttpBody("Hello world!");
        final var expected = HttpResponse.builder()
                .statusLine(new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.OK))
                .contentType(TEXT_HTML.defaultCharset())
                .body(body)
                .build();
        assertThat(actual.asString()).isEqualTo(expected.asString());
    }

    @Test
    @DisplayName("Handle index.")
    void index() throws IOException {
        // given
        final String path = "/index.html";
        final String input = String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var request = new HttpRequest(input);

        final var sut = FrontController.getInstance();

        // when
        final var actual = sut.handle(request);

        // then
        final var body = bodyFromResource(path);
        final var expected = HttpResponse.builder()
                .statusLine(new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.OK))
                .contentType(TEXT_HTML.defaultCharset())
                .body(body)
                .build();
        assertThat(actual.asString()).isEqualTo(expected.asString());
    }

    @Test
    @DisplayName("Handle login.")
    void login() throws IOException {
        // given
        final String path = "/login.html";
        final String input = String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var request = new HttpRequest(input);

        final var sut = FrontController.getInstance();

        // when
        final var actual = sut.handle(request);

        // then
        final var body = bodyFromResource(path);
        final var expected = HttpResponse.builder()
                .statusLine(new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.OK))
                .contentType(TEXT_HTML.defaultCharset())
                .body(body)
                .build();
        assertThat(actual.asString()).isEqualTo(expected.asString());
    }

    @Test
    @DisplayName("Handle register.")
    void register() throws IOException {
        final String path = "/register.html";
        final String input = String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var request = new HttpRequest(input);

        final var sut = FrontController.getInstance();

        // when
        final var actual = sut.handle(request);

        // then
        final var body = bodyFromResource(path);
        final var expected = HttpResponse.builder()
                .statusLine(new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.OK))
                .contentType(TEXT_HTML.defaultCharset())
                .body(body)
                .build();
        assertThat(actual.asString()).isEqualTo(expected.asString());

    }

    HttpBody bodyFromResource(final String path) throws IOException {
        final var resource = getClass().getClassLoader().getResource("static" + path);
        final var fileLocation = Objects.requireNonNull(resource).getFile();
        final var filepath = new File(fileLocation).toPath();
        final var bytes = Files.readAllBytes(filepath);
        final var string = new String(bytes);
        return new HttpBody(string);
    }
}
