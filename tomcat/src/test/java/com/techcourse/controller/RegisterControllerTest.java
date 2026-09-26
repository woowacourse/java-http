package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private final RegisterController controller =
            new RegisterController();

    @Test
    void GET_요청이면_회원가입_페이지로_forward한다() throws Exception {
        // given
        final HttpRequest request = HttpRequest.from(new ByteArrayInputStream(
                "GET /register HTTP/1.1\r\nHost: localhost:8080\r\n\r\n".getBytes(StandardCharsets.UTF_8)
        )).orElseThrow();
        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.getForwardPath()).contains("/register.html");
    }

    @Test
    void POST_요청으로_회원을_등록하고_index로_리다이렉트한다()
            throws Exception {

        // given
        final String account =
                "register-controller-test";

        final String body =
                "account="
                        + account
                        + "&password=1234"
                        + "&email=moca%40email.com";

        final HttpRequest request =
                createPostRequest(
                        "/register",
                        body
                );

        final HttpResponse response =
                new HttpResponse();

        // when
        controller.service(
                request,
                response
        );

        // then
        final Optional<User> savedUser =
                InMemoryUserRepository
                        .findByAccount(
                                account
                        );

        assertThat(savedUser)
                .isPresent();

        assertThat(
                savedUser
                        .orElseThrow()
                        .getAccount()
        ).isEqualTo(
                account
        );

        final String result =
                writeResponse(response);

        assertThat(result)
                .contains(
                        "HTTP/1.1 302 Found"
                )
                .contains(
                        "Location: /index.html"
                );
    }

    @Test
    void 회원가입_파라미터가_누락되면_회원을_저장하지_않고_회원가입_페이지로_forward한다()
            throws Exception {

        // given
        final String account =
                "invalid-register-controller-test";

        final String body =
                "account="
                        + account
                        + "&password=1234";

        final HttpRequest request =
                createPostRequest(
                        "/register",
                        body
                );

        final HttpResponse response =
                new HttpResponse();

        // when
        controller.service(
                request,
                response
        );

        // then
        assertThat(
                InMemoryUserRepository
                        .findByAccount(
                                account
                        )
        ).isEmpty();

        assertThat(response.getForwardPath()).contains("/register.html");
    }

    @Test
    void 계정이_공백이면_회원을_저장하지_않고_회원가입_페이지로_forward한다() throws Exception {
        assertRegisterRejected("account=+++&password=1234&email=moca%40email.com", "   ");
    }

    @Test
    void 비밀번호가_공백이면_회원을_저장하지_않고_회원가입_페이지로_forward한다() throws Exception {
        assertRegisterRejected(
                "account=blank-password-test&password=+++&email=moca%40email.com",
                "blank-password-test"
        );
    }

    @Test
    void 이메일이_공백이면_회원을_저장하지_않고_회원가입_페이지로_forward한다() throws Exception {
        assertRegisterRejected(
                "account=blank-email-test&password=1234&email=+++",
                "blank-email-test"
        );
    }

    private void assertRegisterRejected(final String body, final String account) throws Exception {
        // given
        final HttpRequest request = createPostRequest("/register", body);
        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
        assertThat(response.getForwardPath()).contains("/register.html");
    }

    private HttpRequest createPostRequest(
            final String path,
            final String body
    ) throws Exception {

        final String rawRequest =
                String.join(
                        "\r\n",
                        "POST " + path + " HTTP/1.1",
                        "Host: localhost:8080",
                        "Content-Length: "
                                + body.getBytes(
                                StandardCharsets.UTF_8
                        ).length,
                        "Content-Type: application/x-www-form-urlencoded",
                        "",
                        body
                );

        return HttpRequest.from(
                new ByteArrayInputStream(
                        rawRequest.getBytes(
                                StandardCharsets.UTF_8
                        )
                )
        ).orElseThrow();
    }

    private String writeResponse(
            final HttpResponse response
    ) throws Exception {

        final ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        response.writeTo(
                outputStream
        );

        return outputStream.toString(
                StandardCharsets.UTF_8
        );
    }
}