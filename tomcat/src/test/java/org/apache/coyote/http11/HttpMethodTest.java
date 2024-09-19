package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("문자열을 바탕으로 HttpMethod를 반환한다.")
    @Test
    void from() {
        String get = "GET";
        String post = "POST";
        String put = "PUT";
        String delete = "DELETE";
        String patch = "PATCH";
        String options = "OPTIONS";

        HttpMethod getMethod = HttpMethod.from(get);
        HttpMethod postMethod = HttpMethod.from(post);
        HttpMethod putMethod = HttpMethod.from(put);
        HttpMethod deleteMethod = HttpMethod.from(delete);
        HttpMethod patchMethod = HttpMethod.from(patch);
        HttpMethod optionsMethod = HttpMethod.from(options);

        assertAll(
                () -> assertThat(getMethod).isEqualTo(HttpMethod.GET),
                () -> assertThat(postMethod).isEqualTo(HttpMethod.POST),
                () -> assertThat(putMethod).isEqualTo(HttpMethod.PUT),
                () -> assertThat(deleteMethod).isEqualTo(HttpMethod.DELETE),
                () -> assertThat(patchMethod).isEqualTo(HttpMethod.PATCH),
                () -> assertThat(optionsMethod).isEqualTo(HttpMethod.OPTIONS)
        );
    }

    @DisplayName("지원하지 않는 메서드를 사용하면 에러가 발생한다.")
    @Test
    void ofNotFound() {
        String method = "WRONG";

        assertThatCode(() -> HttpMethod.from(method))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
