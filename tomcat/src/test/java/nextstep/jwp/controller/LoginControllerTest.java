package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LoginControllerTest {

    @ParameterizedTest
    @CsvSource({"invalidAccount,password", "gugu,invalidPassword"})
    void loginByInvalidAccount(String account, String password) {
        // given
        final HttpRequest request = new HttpRequest(
                "POST /login?account=" + account + "&password=" + password + " HTTP/1.1",
                Map.of("Host", "localhost:8080",
                        "Accept", "text/html",
                        "Connection", "keep-alive"),
                "");

        // when
        final HttpResponse actual = new LoginController().doService(request);

        // then
        HttpResponse response = HttpResponse.redirect("/401.html").build();
        response.addHeader("Location", "/401.html");
        assertThat(actual).isEqualTo(response);
    }
}
