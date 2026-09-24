package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUriTest {

    @Test
    @DisplayName("쿼리 스트링이 포함된 URI에서 경로와 파라미터를 분리한다.")
    void parseQueryParameters() {
        // when
        RequestUri requestUri = new RequestUri("/login?account=gugu&password=password");

        // then
        assertThat(requestUri.getPath()).isEqualTo("/login");
        assertThat(requestUri.getResourcePath()).isEqualTo("/login.html");
        assertThat(requestUri.findParameter("account")).contains("gugu");
        assertThat(requestUri.findParameter("password")).contains("password");
    }

    @Test
    @DisplayName("루트 경로는 index.html 정적 리소스 경로로 변환한다.")
    void normalizeRootPath() {
        // when
        RequestUri requestUri = new RequestUri("/");

        // then
        assertThat(requestUri.getPath()).isEqualTo("/");
        assertThat(requestUri.getResourcePath()).isEqualTo("/index.html");
    }
}
