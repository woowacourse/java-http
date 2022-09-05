package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.generator.LoginResponseGenerator;
import org.apache.coyote.http11.response.generator.ResponseGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LoginResponseGeneratorTest {

    private static final ResponseGenerator LOGIN_RESPONSE_GENERATOR = new LoginResponseGenerator();

    @DisplayName("처리할 수 있는 HttpRequest인지 반환한다.")
    @ParameterizedTest
    @CsvSource({"GET /login?account=gugu&password=password HTTP/1.1, true", "GET /js/scripts.js HTTP/1.1, false"})
    void isSuitable(String request, boolean expected) {
        boolean actual = LOGIN_RESPONSE_GENERATOR.isSuitable(HttpRequest.from(request));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("HttpResponse를 반환한다.")
    @ParameterizedTest
    @CsvSource({"GET /login?account=gugu&password=password HTTP/1.1, http://localhost:8080/index.html",
            "GET /login?account=gugu&password=password123 HTTP/1.1, http://localhost:8080/401.html",
            "GET /login?account=gugu123&password=password HTTP/1.1, http://localhost:8080/401.html"})
    void generate(String request, String redirectLocation) throws IOException {
        HttpRequest httpRequest = HttpRequest.from(request);

        HttpResponse httpResponse = LOGIN_RESPONSE_GENERATOR.generate(httpRequest);

        assertThat(httpResponse.getResponse())
                .contains("302 Found")
                .contains("Location: " + redirectLocation);
    }
}