package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void showsRegisterPage() throws Exception {
        //given
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpRequest request = request("GET", "");

        //when
        new RegisterController().service(request, new HttpResponse(output));

        //then
        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("<title>회원가입</title>");
    }

    @Test
    void savesUserAndRedirectsAfterPost() throws Exception {
        //given
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpRequest request = request("POST", "account=controller-user&password=secret&email=new%40example.com");

        //when
        new RegisterController().service(request, new HttpResponse(output));

        //then
        assertThat(InMemoryUserRepository.findByAccount("controller-user"))
                .hasValueSatisfying(user -> assertThat(user.checkPassword("secret")).isTrue());
        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found\r\nLocation: /index.html\r\n");
    }

    @Test
    void rejectsMissingRequiredParameter() throws Exception {
        //given
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpRequest request = request("POST", "account=missing-email&password=secret");

        //when
        new RegisterController().service(request, new HttpResponse(output));

        //then
        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 400 Bad Request\r\n")
                .doesNotContain("Location:");
    }

    private HttpRequest request(String method, String body) {
        return new HttpRequest(new RequestLine(method, "/register", "", "HTTP/1.1"), Map.of(), body);
    }
}
