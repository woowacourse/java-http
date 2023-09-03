package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestPathTest {

    @Test
    @DisplayName("RequestPath를 생성한다.")
    void creatRequestPath() {
        //given
        final String path = "/login?account=gugu&password=password";

        //when
        final RequestPath requestPath = RequestPath.from(path);

        //then
        final Map<String, String> expectedQueryString = new HashMap<>();
        expectedQueryString.put("account", "gugu");
        expectedQueryString.put("password", "password");

        SoftAssertions.assertSoftly((softly -> {
            assertThat(requestPath.getPath()).isEqualTo("/login");
            assertThat(requestPath.getQueryString()).isEqualTo(expectedQueryString);
        }));
    }

}
