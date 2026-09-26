package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.HttpSessionHandler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private static final String USER_SESSION_KEY = "user";

    private final SessionManager sessionManager =
            SessionManager.getInstance();

    private final HttpSessionHandler sessionHandler =
            new HttpSessionHandler(sessionManager);

    private final LoginController controller =
            new LoginController(sessionHandler);

    @Test
    void 로그인하지_않은_사용자가_GET_login을_요청하면_응답을_결정하지_않는다()
            throws Exception {

        // given
        final HttpRequest request =
                createRequest(
                        String.join(
                                "\r\n",
                                "GET /login HTTP/1.1",
                                "Host: localhost:8080",
                                "",
                                ""
                        )
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
                response.hasStatus()
        ).isFalse();
    }

    @Test
    void 로그인된_사용자가_GET_login을_요청하면_index로_리다이렉트한다()
            throws Exception {

        // given
        final HttpSession session =
                sessionManager.createSession();

        final User user =
                InMemoryUserRepository
                        .findByAccount("gugu")
                        .orElseThrow();

        session.setAttribute(
                USER_SESSION_KEY,
                user
        );

        final HttpRequest request =
                createRequest(
                        String.join(
                                "\r\n",
                                "GET /login HTTP/1.1",
                                "Host: localhost:8080",
                                "Cookie: JSESSIONID="
                                        + session.getId(),
                                "",
                                ""
                        )
                );

        final HttpResponse response =
                new HttpResponse();

        try {
            // when
            controller.service(
                    request,
                    response
            );

            // then
            final String result =
                    writeResponse(response);

            assertThat(result)
                    .contains(
                            "HTTP/1.1 302 Found"
                    )
                    .contains(
                            "Location: /index.html"
                    );

        } finally {
            session.invalidate();
        }
    }

    @Test
    void 올바른_계정과_비밀번호로_로그인하면_세션에_User를_저장하고_index로_리다이렉트한다()
            throws Exception {

        // given
        final String body =
                "account=gugu&password=password";

        final HttpRequest request =
                createPostRequest(
                        "/login",
                        body
                );

        final HttpResponse response =
                new HttpResponse();

        HttpSession createdSession = null;

        try {
            // when
            controller.service(
                    request,
                    response
            );

            // then
            final String result =
                    writeResponse(response);

            assertThat(result)
                    .contains(
                            "HTTP/1.1 302 Found"
                    )
                    .contains(
                            "Location: /index.html"
                    )
                    .contains(
                            "Set-Cookie: JSESSIONID="
                    );

            final String sessionId =
                    extractSessionId(result);

            createdSession =
                    sessionManager.findSession(
                            sessionId
                    );

            assertThat(createdSession)
                    .isNotNull();

            final Object value =
                    createdSession.getAttribute(
                            USER_SESSION_KEY
                    );

            assertThat(value)
                    .isInstanceOf(
                            User.class
                    );

            final User loginUser =
                    (User) value;

            assertThat(
                    loginUser.getAccount()
            ).isEqualTo(
                    "gugu"
            );

        } finally {
            if (createdSession != null) {
                createdSession.invalidate();
            }
        }
    }

    @Test
    void 비밀번호가_틀리면_401페이지로_리다이렉트한다()
            throws Exception {

        // given
        final String body =
                "account=gugu&password=wrong";

        final HttpRequest request =
                createPostRequest(
                        "/login",
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
        final String result =
                writeResponse(response);

        assertThat(result)
                .contains(
                        "HTTP/1.1 302 Found"
                )
                .contains(
                        "Location: /401.html"
                )
                .doesNotContain(
                        "Set-Cookie: JSESSIONID="
                );
    }

    @Test
    void 로그인_파라미터가_누락되면_401페이지로_리다이렉트한다()
            throws Exception {

        // given
        final String body =
                "account=gugu";

        final HttpRequest request =
                createPostRequest(
                        "/login",
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
        final String result =
                writeResponse(response);

        assertThat(result)
                .contains(
                        "HTTP/1.1 302 Found"
                )
                .contains(
                        "Location: /401.html"
                );
    }

    private HttpRequest createPostRequest(
            final String path,
            final String body
    ) throws Exception {

        return createRequest(
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
                )
        );
    }

    private HttpRequest createRequest(
            final String rawRequest
    ) throws Exception {

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

    private String extractSessionId(
            final String response
    ) {
        return response.lines()
                .filter(line ->
                        line.startsWith(
                                "Set-Cookie: JSESSIONID="
                        )
                )
                .map(line ->
                        line.substring(
                                "Set-Cookie: JSESSIONID="
                                        .length()
                        ).trim()
                )
                .findFirst()
                .orElseThrow();
    }
}