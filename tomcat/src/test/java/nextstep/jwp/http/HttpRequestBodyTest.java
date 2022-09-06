package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;

class HttpRequestBodyTest {

    @Test
    void 빈_body가_입력되면_빈_Map이_생성된다() {
        HttpRequestBody actual = HttpRequestBody.from("");

        assertThat(actual.getValues()).isEmpty();
    }

    @Test
    void body가_입력되면_request_body에_parsing된다() {
        HttpRequestBody expected = new HttpRequestBody(Map.of("account", "gugu",
                "password", "password",
                "email", "hkkang%40woowahan.com"));
        HttpRequestBody actual = HttpRequestBody.from("account=gugu&password=password&email=hkkang%40woowahan.com");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 존재하지않는_key의_value를_반환하려하는_경우_예외가_발생한다() {
        HttpRequestBody httpRequestBody = HttpRequestBody.empty();
        assertThatThrownBy(() -> httpRequestBody.getValue("account"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("존재하지 않는 key 입니다.");
    }
}
