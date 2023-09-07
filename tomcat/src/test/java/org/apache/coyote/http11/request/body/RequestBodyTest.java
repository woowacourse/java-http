package org.apache.coyote.http11.request.body;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    @DisplayName("RequestBody에 담긴 내용을 파싱하여 저장한다.")
    void stringToHttpCookie() {
        // given
        final String bodyString = "account=gugu&password=password";

        // when
        final RequestBody actual = RequestBody.from(bodyString);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getRequestBody()).hasSize(2);
            softAssertions.assertThat(actual.getRequestBody().get("account")).isEqualTo("gugu");
            softAssertions.assertThat(actual.getRequestBody().get("password")).isEqualTo("password");
        });
    }
}
