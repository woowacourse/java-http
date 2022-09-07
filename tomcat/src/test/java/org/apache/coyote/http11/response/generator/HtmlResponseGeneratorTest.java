package org.apache.coyote.http11.response.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HtmlResponseGeneratorTest {

    private static final Manager SESSION_MANAGER = SessionManager.getInstance();
    private static final HtmlResponseGenerator HTML_RESPONSE_GENERATOR =
            new HtmlResponseGenerator(SESSION_MANAGER);

    @DisplayName("처리할 수 있는 HttpRequest인지 반환한다.")
    @ParameterizedTest
    @MethodSource("provideRequestAndExpected")
    void isSuitable(String request, boolean expected) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        boolean actual = HTML_RESPONSE_GENERATOR.isSuitable(HttpRequest.from(bufferedReader));

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideRequestAndExpected() {
        return Stream.of(
                Arguments.of(
                        "GET /index.html HTTP/1.1\n" +
                                "Host: localhost:8080\n" +
                                "Connection: keep-alive\n" +
                                "",
                        true),
                Arguments.of(
                        "GET /js/scripts.js HTTP/1.1\n" +
                                "Host: localhost:8080\n" +
                                "Connection: keep-alive\n" +
                                "",
                        false)
        );
    }

    @DisplayName("HttpResponse를 반환한다.")
    @Test
    void generate() throws IOException {
        String request = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpResponse httpResponse = HTML_RESPONSE_GENERATOR.generate(HttpRequest.from(bufferedReader));

        assertThat(httpResponse.getResponse())
                .contains("200 OK")
                .contains("Content-Type: text/html;charset=utf-8 ")
                .contains("Content-Length: 5564");
    }

    @DisplayName("이미 로그인을 해서 JSESSIONID를 가진 사용자가 다시 로그인 페이지에 접근하면 index.html로 리다이렉트 시킨다.")
    @Test
    void generate_RedirectToIndexHtml() throws IOException {
        User user = new User("chris", "password", "email@google.com");
        Session session = new Session("user", user);
        SESSION_MANAGER.add(session);

        String request = "GET /login HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Cookie: JSESSIONID=" + session.getId() +
                "";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpResponse httpResponse = HTML_RESPONSE_GENERATOR.generate(HttpRequest.from(bufferedReader));

        assertThat(httpResponse.getResponse())
                .contains("302 Found ")
                .contains("Location: http://localhost:8080/index.html");
    }
}
