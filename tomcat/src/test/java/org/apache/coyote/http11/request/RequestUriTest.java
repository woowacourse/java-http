package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestUriTest {

    @Test
    @DisplayName("RequestUri에서 QueryParam을 파싱한다.")
    void parseQueryParams() {
        // given
        RequestUri requestUri = new RequestUri("/login?account=gugu&password=password");

        // when
        Map<String, Object> queryParams = requestUri.parseQueryParams();

        // then
        assertThat(queryParams.get("account")).isEqualTo("gugu");
        assertThat(queryParams.get("password")).isEqualTo("password");
    }
}
