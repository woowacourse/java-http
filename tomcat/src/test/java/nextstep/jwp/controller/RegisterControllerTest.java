package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterControllerTest extends ControllerTest {

    @AfterEach
    void setUp() {
        InMemoryUserRepository.clear();
    }

    @DisplayName("회원가입에 성공하면 index.html로 리다이렉트한다.")
    @Test
    void register_success() throws Exception {
        // given
        String requestValue = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=tonic&password=password&email=tonic@woowahan.com");
        HttpRequest request = HttpRequest.from(toBufferedReader(requestValue));

        // when
        HttpResponse response = HandlerMapping.getMethodHandler(request)
                .service();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/index.html");
        assertThat(InMemoryUserRepository.findByAccount("tonic")).isPresent();
    }

    @DisplayName("회원가입에 실패하면 400.html로 리다이렉트 한다.")
    @Test
    void register_fail() throws Exception {
        // given
        String requestValue = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=tonic&password=password&email=tonic@woowahan.com");
        HttpRequest request = HttpRequest.from(toBufferedReader(requestValue));
        InMemoryUserRepository.save(new User("tonic", "password", "tonic@woowahan.com"));

        // when
        HttpResponse response = HandlerMapping.getMethodHandler(request)
                .service();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/static/400.html");
        assertThat(InMemoryUserRepository.findByAccount("tonic")).isPresent();
    }

}
