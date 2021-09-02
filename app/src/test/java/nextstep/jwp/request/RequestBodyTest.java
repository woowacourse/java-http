package nextstep.jwp.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    @DisplayName("params로 들어온 body를 파싱한다.")
    void parseRequestBodyParams() {
        RequestBody requestBody = new RequestBody("account=gugu&password=password&email=hkkang%40woowahan.com ");

        Map<String, String> params = requestBody.getParams();

        assertThat(params.get("account")).isEqualTo("gugu");
        assertThat(params.get("password")).isEqualTo("password");
        assertThat(params.get("email")).isEqualTo("hkkang%40woowahan.com");
    }
}