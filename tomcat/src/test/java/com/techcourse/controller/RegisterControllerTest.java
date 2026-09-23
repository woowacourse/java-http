package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private final RegisterController controller =
            new RegisterController();

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
    void 회원가입_파라미터가_누락되면_회원을_저장하지_않고_응답을_결정하지_않는다()
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

        assertThat(
                response.hasStatus()
        ).isFalse();
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