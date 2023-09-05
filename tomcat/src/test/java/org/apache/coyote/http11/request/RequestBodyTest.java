package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestBodyTest {

    @Test
    void requestBody를_입력해_생성한다() {
        // given
        String requestBody = "account=gugu&password=password&email=hkkang@woowahan.com";

        // when
        RequestBody body = RequestBody.from(requestBody);

        // then
        assertThat(body).usingRecursiveComparison()
                .isEqualTo(
                        new RequestBody(Map.of(
                                "account", "gugu",
                                "password", "password",
                                "email", "hkkang@woowahan.com"
                        ))
                );
    }

    @Test
    void body_field_이름을_입력하면_value를_반환한다() {
        // given
        String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        RequestBody body = RequestBody.from(requestBody);

        // when
        String value = body.get("account");

        // then
        assertThat(value).isEqualTo("gugu");
    }
}
