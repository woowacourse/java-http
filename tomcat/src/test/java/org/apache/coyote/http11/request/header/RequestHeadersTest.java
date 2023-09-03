package org.apache.coyote.http11.request.header;

import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    @DisplayName("문자열로 주어진 요청 헤더 각 요소의 name과 value를 Map의 형태로 저장한다.")
    void stringToRequestHeader() {
        // given
        final String givenRequestHeader = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );

        // when
        final RequestHeaders actual = RequestHeaders.from(givenRequestHeader);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getRequestHeader()).hasSize(2);
            softAssertions.assertThat(actual.getRequestHeader().get("Host")).isEqualTo("localhost:8080");
            softAssertions.assertThat(actual.getRequestHeader().get("Connection")).isEqualTo("keep-alive");
        });
    }
}
