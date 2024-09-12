package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestGeneratorTest {

    @DisplayName("Request 생성 테스트")
    @Test
    void acceptTest() throws IOException {
        // given
        BufferedReader reader = new BufferedReader(new StringReader(
                """
                        POST /login?id=123&password=123 HTTP/1.1
                        Host: localhost:8080
                        Accept: text/html
                        Content-Length: 0
                        Cookie: JSESSIONID=1234567890; _ga_DJT9END770=GS1.1.1703907885.1.1.1703907923.0.0.0
                        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
                                                
                        """));

        // when
        HttpRequest request = RequestGenerator.accept(reader);

        // then
        assertAll(
                () -> assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(request.getUri()).isEqualTo("/login"),
                () -> assertThat(request.getHeader().getContentLength()).isEqualTo(0),
                () -> assertThat(request.getParameter("id")).isEqualTo("123"),
                () -> assertThat(request.getParameter("password")).isEqualTo("123"),
                () -> assertThat(request.getCookie().get("JSESSIONID").get()).isEqualTo("1234567890"),
                () -> assertThat(request.getCookie().get("_ga_DJT9END770").get()).isEqualTo(
                        "GS1.1.1703907885.1.1.1703907923.0.0.0")
        );
    }
}
