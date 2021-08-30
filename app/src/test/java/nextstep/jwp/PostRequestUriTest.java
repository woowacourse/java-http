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

public class PostRequestUriTest {
    @Test
    @DisplayName("로그인 POST 요청 성공")
    void createLoginResponse() throws IOException {
        final URL resource = PostRequestUri.class.getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "302 Found" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Location: /index.html ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String requestBody = "account=gugu&password=password";
        String response = PostRequestUri.createResponse("/login", requestBody);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 POST 요청 실패 - 존재하지 않는 계정")
    void createLoginFailureResponse() throws IOException {
        final URL resource = PostRequestUri.class.getClassLoader().getResource("static/401.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "302 Found" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Location: /401.html ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String requestBody = "account=gugu&password=password1234";
        String response = PostRequestUri.createResponse("/login", requestBody);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 POST 요청 실패 - 유효하지 않은 비밀번호")
    void createLoginWithInvalidPasswordFailureResponse() throws IOException {
        final URL resource = PostRequestUri.class.getClassLoader().getResource("static/401.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "302 Found" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Location: /401.html ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String requestBody = "account=gugu&password=asdf";
        String response = PostRequestUri.createResponse("/login", requestBody);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원가입 POST 요청")
    void createRegisterResponse() throws IOException {
        final URL resource = PostRequestUri.class.getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        String result = Files.readAllLines(path).stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "302 Found" + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Location: /index.html ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);

        String requestBody = "account=fafi&password=password&email=asdf@naver.com";
        String response = PostRequestUri.createResponse("/register", requestBody);

        assertThat(response).isEqualTo(expected);
    }
}
