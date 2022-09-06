package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void registerByDuplicatedAccount() {
        // given
        final HttpRequest request = new HttpRequest("POST /register HTTP/1.1",
                Map.of("Host", "localhost:8080",
                        "Content-Length", "66",
                        "Content-Type", "application/x-www-form-urlencoded"),
                "account=gugu&password=password&email=gugu@woowahan.com");
        RegisterController sut = new RegisterController();

        // when
        final HttpResponse response = sut.doPost(request);

        // then
        assertThat(response).isEqualTo(HttpResponse.redirect("/register").build());
    }
}
