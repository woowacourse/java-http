package org.apache.coyote.http11.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RequestUriTest {

    @Test
    void String_RequestURI_를_입력받아_객체를_생성하고_그_안에는_Path와_QueryString으로_나눠진다() {
        //given
        final String requestUriValue = "/path?a=1&b=2";
        //when
        RequestUri requestUri = RequestUri.from(requestUriValue);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(requestUri.getPath()).isEqualTo("/path");
            softAssertions.assertThat(requestUri.getQueryString()).isEqualTo("a=1&b=2");
        });
    }

}
