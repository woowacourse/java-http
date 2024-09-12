package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.HttpStatusCode;
import com.techcourse.model.User;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final InMemoryUserRepository inMemoryUserRepository = InMemoryUserRepository.getInstance();
    private final LoginController loginController = new LoginController();

    @Nested
    class doGet {
        @DisplayName("로그인하지 않은 상태에서 200 상태 코드를 반환한다.")
        @Test
        void notLogin() throws IOException {
            HttpResponse response = new HttpResponse();
            HttpRequest request = new HttpRequest();

            loginController.doGet(request, response);

            assertEquals(HttpStatusCode.OK, response.getHttpStatusCode());
        }

        @DisplayName("이미 로그인을 했을 경우 index.html로 302 상태 코드를 반환한다.")
        @Test
        void alreadyLogin() throws IOException {
            inMemoryUserRepository.save(new User("mark", "1234", "asd@asd.asd"));
            HttpResponse response = new HttpResponse();
            HttpRequest request = new HttpRequest();
            request.setCookie("JSESSIONID", "1234");

            loginController.doGet(request, response);

            assertAll(
                    () -> assertEquals(HttpStatusCode.FOUND, response.getHttpStatusCode()),
                    () -> assertEquals("/index.html", response.getHeaders().get("Location"))
            );
        }
    }

    @Nested
    class doPost {
        @DisplayName("로그인 성공 시 index.html로 302 상태 코드를 반환한다.")
        @Test
        void login() {
            inMemoryUserRepository.save(new User("mark", "1234", "asd@asd.asd"));
            HttpResponse response = new HttpResponse();
            HttpRequest request = new HttpRequest();
            request.setParameter("account", "mark");
            request.setParameter("password", "1234");

            loginController.doPost(request, response);

            assertAll(
                    () -> assertEquals(HttpStatusCode.FOUND, response.getHttpStatusCode()),
                    () -> assertEquals("/index.html", response.getHeaders().get("Location")),
                    () -> assertNotNull(response.getHeaders().getCookie("JSESSIONID"))
            );
        }

        @DisplayName("이미 로그인을 했을 경우 index.html로 302 상태 코드를 반환한다.")
        @Test
        void alreadyLogin() throws IOException {
            inMemoryUserRepository.save(new User("mark", "1234", "asd@asd.asd"));
            HttpResponse response = new HttpResponse();
            HttpRequest request = new HttpRequest();
            request.setCookie("JSESSIONID", "1234");

            loginController.doGet(request, response);

            assertAll(
                    () -> assertEquals(HttpStatusCode.FOUND, response.getHttpStatusCode()),
                    () -> assertEquals("/index.html", response.getHeaders().get("Location"))
            );
        }

        @DisplayName("로그인 시 비밀번호가 다르면 401.html로 302 상태 코드를 반환한다.")
        @Test
        void loginFail() {
            inMemoryUserRepository.save(new User("mark", "1234", "asd@asd.asd"));
            HttpResponse response = new HttpResponse();
            HttpRequest request = new HttpRequest();
            request.setParameter("account", "mark");
            request.setParameter("password", "asdf");

            loginController.doPost(request, response);

            assertAll(
                    () -> assertEquals(HttpStatusCode.FOUND, response.getHttpStatusCode()),
                    () -> assertEquals("/401.html", response.getHeaders().get("Location"))
            );
        }
    }
}
