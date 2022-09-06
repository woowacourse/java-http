package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.jwp.util.FileReader;

class LoginControllerTest {

    private final LoginController controller = new LoginController();

    @Nested
    @DisplayName("get 요청시")
    class Get {

        @Test
        @DisplayName("login.html을 반환한다.")
        void loginGet() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generate("GET", "/login");

            // when
            HttpResponse response = controller.doGet(request);

            // then
            String responseString = response.toResponseString();

            assertThat(responseString).contains("200 OK");
            assertThat(responseString).contains(FileReader.read("/login.html"));
        }

        @Test
        @DisplayName("쿠키에 세션값이 포함되어있다면 index.html로 리다이렉트 한다.")
        void containsSession() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generateWithCookie("GET", "/login");

            // when
            HttpResponse response = controller.doGet(request);

            // then
            String responseString = response.toResponseString();

            assertThat(responseString).contains("302 Found");
            assertThat(responseString).contains("Location: /index.html");
        }
    }

    @Nested
    @DisplayName("post 요청시")
    class Post {

        @Test
        @DisplayName("올바른 사용자라면 index.html로 리다이렉트 한다.")
        void loginPost() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generateWithBody("POST", "/login",
                "account=gugu&password=password");

            // when
            HttpResponse response = controller.doPost(request);

            // then
            String responseString = response.toResponseString();

            assertThat(responseString).contains("302 Found");
            assertThat(responseString).contains("Location: /index.html");
        }

        @Test
        @DisplayName("올바르지 않은 password라면 401.html로 리다이렉트 한다.")
        void invalidPassword() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generateWithBody("POST", "/login",
                "account=gugu&password=invalidPassword");

            // when
            HttpResponse response = controller.doPost(request);

            // then
            String responseString = response.toResponseString();

            assertThat(responseString).contains("302 Found");
            assertThat(responseString).contains("Location: /401.html");
        }

        @Test
        @DisplayName("올바르지 않은 account라면 401.html로 리다이렉트 한다.")
        void invalidAccount() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generateWithBody("POST", "/login",
                "account=ari&password=password");

            // when
            HttpResponse response = controller.doPost(request);

            // then
            String responseString = response.toResponseString();

            assertThat(responseString).contains("302 Found");
            assertThat(responseString).contains("Location: /401.html");
        }

        @Test
        @DisplayName("쿠키에 세션값이 포함되어있다면 index.html로 리다이렉트 한다.")
        void containsSession() throws IOException {
            // given
            HttpRequest request = HttpRequestGenerator.generateWithCookie("POST", "/login");

            // when
            HttpResponse response = controller.doPost(request);

            // then
            String responseString = response.toResponseString();

            assertThat(responseString).contains("302 Found");
            assertThat(responseString).contains("Location: /index.html");
        }
    }
}
