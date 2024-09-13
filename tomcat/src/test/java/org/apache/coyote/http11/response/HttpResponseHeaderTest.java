package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseHeaderTest {

    @Test
    @DisplayName("쿠키를 추가한다.")
    void addCookie() {
        HttpResponseHeader header = new HttpResponseHeader();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("JSESSIONID", "sessionId");
        map.put("yummy_cookie", "choco");
        header.putCookie("Cookie", map);

        assertThat(header.getHeaders().get("Cookie")).isEqualTo("JSESSIONID=sessionId; yummy_cookie=choco");
    }
}
