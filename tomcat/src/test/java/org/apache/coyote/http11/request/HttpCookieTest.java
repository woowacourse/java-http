package org.apache.coyote.http11.request;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("헤더의 구분자를 기준으로 잘 구분이 되는지 확인한다")
    @Test
    void from() {
        List<String> requestHeader = List.of(
                "Host: www.example.org",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry"
        );

        HttpHeaders headers = HttpHeaders.from(requestHeader);
        String yummy = headers.getCookieValue("yummy_cookie");
        String tasty = headers.getCookieValue("tasty_cookie");

        Assertions.assertThat(yummy).isEqualTo("choco");
        Assertions.assertThat(tasty).isEqualTo("strawberry");
    }
}
