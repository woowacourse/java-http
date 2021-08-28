package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Request Body")
class RequestBodyTest {
    private static final String REQUEST_BODY_LINE = "account=gugu&password=password&email=hkkang@woowahan.com";

    @DisplayName("Request Line 객체를 성공적으로 생성한다.")
    @Test
    void create() {
        assertDoesNotThrow(() -> {
            new RequestBody(REQUEST_BODY_LINE);
        });
    }

    @DisplayName("key로 param을 얻는다.")
    @Test
    void getParam() {
        RequestBody requestBody = new RequestBody(REQUEST_BODY_LINE);

        String account = requestBody.getParam("account");
        String password = requestBody.getParam("password");
        String email = requestBody.getParam("email");

        assertThat(account).isEqualTo("gugu");
        assertThat(password).isEqualTo("password");
        assertThat(email).isEqualTo("hkkang@woowahan.com");
    }
}