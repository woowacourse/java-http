package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class QueryParameterTest {

    @Test
    @DisplayName("입력된 query parameter의 값을 얻을 수 있다.")
    void getAttribute() {
        QueryParameter queryParameter = new QueryParameter("/login?account=gugu&password=password&email=hkkang%40woowahan.com");

        assertSoftly(softly -> {
            softly.assertThat(queryParameter.getAttribute("account")).isEqualTo("gugu");
            softly.assertThat(queryParameter.getAttribute("email")).isEqualTo("hkkang@woowahan.com");
            softly.assertThat(queryParameter.getAttribute("password")).isEqualTo("password");
        });
    }
}
