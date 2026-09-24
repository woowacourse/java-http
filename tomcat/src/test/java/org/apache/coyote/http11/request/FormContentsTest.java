package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormContentsTest {

    @Test
    @DisplayName("폼 본문을 파싱하고 URL 인코딩된 값을 디코딩한다.")
    void parseFormContents() {
        // when
        FormContents formContents = FormContents.from("account=gugu&email=hkkang%40woowahan.com");

        // then
        assertThat(formContents.find("account")).contains("gugu");
        assertThat(formContents.find("email")).contains("hkkang@woowahan.com");
    }

    @Test
    @DisplayName("잘못된 폼 파라미터는 무시한다.")
    void ignoreMalformedParameter() {
        // when
        FormContents formContents = FormContents.from("account");

        // then
        assertThat(formContents.find("account")).isEmpty();
    }
}
