package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class QueryParameterTest {

    @Test
    @DisplayName("queyr parameter가 비어있다면 예외가 발생한다.")
    void invalidQueryParameter() {
        assertThatThrownBy(() -> new QueryParameter(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("입력된 query parameter의 값을 얻을 수 있다.")
    void get() {
        QueryParameter queryParameter = new QueryParameter("account=gugu&password=password&email=hkkang%40woowahan.com");

        assertSoftly(softly -> {
            softly.assertThat(queryParameter.get("account")).isEqualTo("gugu");
            softly.assertThat(queryParameter.get("email")).isEqualTo("hkkang@woowahan.com");
            softly.assertThat(queryParameter.get("password")).isEqualTo("password");
        });
    }
}
