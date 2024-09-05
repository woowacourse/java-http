package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestTargetTest {

    @DisplayName("reqeust target의 파싱하여 쿼리 파라미터를 추출한다.")
    @Test
    void fromQuery() {
        Http11RequestTarget requestTarget = Http11RequestTarget.from("/login?account=gugu&password=password");

        assertThat(requestTarget.getQueryStrings())
                .containsExactlyInAnyOrderEntriesOf(Map.of("account", List.of("gugu"),
                        "password", List.of("password")));
    }

    @DisplayName("reqeust target의 파싱하여 엔드포인트를 추출한다.")
    @Test
    void fromEndPoint() {
        Http11RequestTarget requestTarget = Http11RequestTarget.from("/login?account=gugu&password=password");

        assertThat(requestTarget.getEndPoint())
                .isEqualTo("/login");
    }
}
