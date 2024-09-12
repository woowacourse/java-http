package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestTargetTest {

    @DisplayName("reqeust target의 파싱하여 엔드포인트를 추출한다.")
    @Test
    void fromEndPoint() {
        Http11RequestTarget requestTarget = Http11RequestTarget.from("/login?account=gugu&password=password");

        assertThat(requestTarget.getEndPoint())
                .isEqualTo("/login");
    }
}
