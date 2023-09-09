package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.apache.coyote.http11.request.body.RequestBody;
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
        assertThat(body).isEqualTo(new RequestBody(
                Map.of(
                        "account", "gugu",
                        "password", "password",
                        "email", "hkkang@woowahan.com"
                )
        ));
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

    @Test
    void body_field_이름을_입력하고_value를_얻을_때_value가_존재하지_않으면_예외를_던진다() {
        // given
        String requestBody = "account=gugu";
        RequestBody body = RequestBody.from(requestBody);

        // expect
        assertThatThrownBy(() -> body.get("wrongField"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("body에 존재하지 않는 field 입니다.");
    }
}
