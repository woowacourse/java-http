package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetRequestUriTest {

    @Test
    @DisplayName("기본 경로에 대해 인덱스 페이지 응답을 생성한다.")
    void createIndexResponseDefault() throws IOException {
        final URL resource = GetRequestUri.class.getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String response = GetRequestUri.createResponse("/");

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName(" \"/index.html\"에 대해 인덱스 페이지 응답을 생성한다.")
    void createIndexResponseWithPath() throws IOException {
        final URL resource = GetRequestUri.class.getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String response = GetRequestUri.createResponse("/index.html");

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 페이지를 응답한다.")
    void createLoginResponse() throws IOException {
        final URL resource = GetRequestUri.class.getClassLoader().getResource("static/login.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String response = GetRequestUri.createResponse("/login");

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원가입 페이지를 응답한다.")
    void createRegisterResponse() throws IOException {
        final URL resource = GetRequestUri.class.getClassLoader().getResource("static/register.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String response = GetRequestUri.createResponse("/register");

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Not Found 페이지를 응답한다.")
    void createNotFoundResponse() throws IOException {
        final URL resource = GetRequestUri.class.getClassLoader().getResource("static/404.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "404 Not Found" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String response = GetRequestUri.createResponse("/invalidPath");

        assertThat(response).isEqualTo(expected);
    }
}
