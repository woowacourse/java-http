package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseHeaderTest {

    @Test
    @DisplayName("쿠키를 추가한다.")
    void addCookie() {
        HttpResponseHeader header = new HttpResponseHeader();

        header.putCookie("Cookie", Map.of("JSESSIONID", "sessionId", "yummy_cookie", "choco"));

        assertThat(header.getHeaders().get("Cookie")).isEqualTo("JSESSIONID=sessionId; yummy_cookie=choco");
    }

}
