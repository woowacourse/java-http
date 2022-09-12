package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void requestLine을_생성한다() {
        RequestLine actual = RequestLine.from("GET /index.html?account=gugu&password=password HTTP/1.1");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("account", "gugu");
        queryParams.put("password", "password");

        assertThat(actual).extracting("httpMethod", "requestUri", "queryParams")
                .containsExactly(HttpMethod.GET, "/index.html", queryParams);
    }
}
