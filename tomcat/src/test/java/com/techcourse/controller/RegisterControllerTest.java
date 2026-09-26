package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static support.ResponseAssertions.assertHtml;
import static support.ResponseAssertions.assertRedirect;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.view.ResourceRenderer;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestLine;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {
    private final RegisterController controller = new RegisterController(new ResourceRenderer());
    private final HttpResponse response = new HttpResponse();
    private final String account = UUID.randomUUID().toString();

    @Test
    void GET_요청_시_회원가입_화면_응답() throws Exception {
        controller.service(request("GET", ""), response);

        assertHtml(response, "/register.html");
    }

    @Test
    void 회원가입_성공_시_사용자_저장과_메인_화면으로_리다이렉트() throws Exception {
        String body = "account=" + account + "&password=password&email=user%40example.com";

        controller.service(request("POST", body), response);

        assertRedirect(response, "/index.html");
        User saved = InMemoryUserRepository.findByAccount(account).orElseThrow();
        assertThat(saved.getAccount()).isEqualTo(account);
        assertThat(saved.checkPassword("password")).isTrue();
        assertThat(saved).extracting("email").isEqualTo("user@example.com");
    }

    @Test
    void 계정_누락_시_회원가입_화면_응답() throws Exception {
        controller.service(request("POST", "password=password&email=user%40example.com"), response);

        assertHtml(response, "/register.html");
    }

    @Test
    void 비밀번호_누락_시_사용자_저장_없이_회원가입_화면_응답() throws Exception {
        assertRegistrationFailure("account=" + account + "&email=user%40example.com");
    }

    @Test
    void 이메일_누락_시_사용자_저장_없이_회원가입_화면_응답() throws Exception {
        assertRegistrationFailure("account=" + account + "&password=password");
    }

    @Test
    void 빈_본문으로_회원가입_요청_시_회원가입_화면_응답() throws Exception {
        controller.service(request("POST", ""), response);

        assertHtml(response, "/register.html");
    }

    private void assertRegistrationFailure(String body) throws Exception {
        controller.service(request("POST", body), response);

        assertHtml(response, "/register.html");
        assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
    }

    private HttpRequest request(String method, String body) {
        return new HttpRequest(new RequestLine(method, "/register", "HTTP/1.1"), Map.of(), body);
    }
}
