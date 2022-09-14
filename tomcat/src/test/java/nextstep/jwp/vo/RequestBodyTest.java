package nextstep.jwp.vo;

import nextstep.jwp.model.FormData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RequestBodyTest {

    @Test
    void parseBody() {
        // given
        String body = "email=bcc0830@naver.com&password=1234&nickname=alpha";

        // when
        RequestBody requestBody = RequestBody.from(body);

        // then
        FormData value = requestBody.getBodies();
        assertAll(
                () -> assertThat(value.get("email")).isEqualTo("bcc0830@naver.com"),
                () -> assertThat(value.get("password")).isEqualTo("1234"),
                () -> assertThat(value.get("nickname")).isEqualTo("alpha")
        );
    }

    @Test
    void parseInvalid() {
        // given
        String body = "email=bcc0830@naver.com&password=1234&nickname=alpha=sd";

        // when
        RequestBody requestBody = RequestBody.from(body);

        // then
        assertThat(requestBody.getBodies().isEmpty()).isTrue();
    }
}
