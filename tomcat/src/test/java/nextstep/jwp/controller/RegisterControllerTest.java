package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.util.FileReader;

class RegisterControllerTest {

    private final RegisterController controller = new RegisterController();

    @Nested
    @DisplayName("get 요청시")
    class Get {

        @Test
        @DisplayName("register.html로 리다이렉트 한다.")
        void registerGet() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generate("GET", "/register");

            // when
            HttpResponse response = controller.doGet(request);

            // then
            String responseString = response.toResponseString();

            assertThat(responseString).contains("200 OK");
            assertThat(responseString).contains(FileReader.read("/register.html"));

        }
    }

    @Nested
    @DisplayName("post 요청시")
    class Post {

        @Test
        @DisplayName("회원가입을 진행하고 index.html로 리다이렉트 한다.")
        void registerPost() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generateWithBody("POST", "/login",
                "account=account&password=password&email=email@email.email");

            // when
            boolean isAlreadySignInBeforeRequest = InMemoryUserRepository.isAlreadySignIn("account", "password",
                "email@email.email");
            HttpResponse response = controller.doPost(request);

            // then
            boolean isAlreadySignInAfterRequest = InMemoryUserRepository.isAlreadySignIn("account", "password",
                "email@email.email");
            String responseString = response.toResponseString();

            assertThat(isAlreadySignInBeforeRequest).isFalse();
            assertThat(isAlreadySignInAfterRequest).isTrue();
            assertThat(responseString).contains("302 Found");
            assertThat(responseString).contains("Location: /index.html");
        }
    }

}
