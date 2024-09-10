package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseBuilderTest {

    @Test
    @DisplayName("빌더를 통해 응답 객체를 생성한다.")
    void createResponse() {
        HttpResponse actual = new HttpResponseBuilder()
                .ok()
                .body("Hello, world!")
                .addHeader("Content-Type", "text/plain")
                .build();
        assertAll(
                () -> assertThat(actual.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(actual.getContent()).isEqualTo("Hello, world!"),
                () -> assertThat(actual.getHeaders().get("Content-Type")).isEqualTo("text/plain"),
                () -> assertThat(actual.getHeaders().get("Content-Length")).isEqualTo(13L)
        );
    }

    @Test
    @DisplayName("statusCode가 설정되지 않은 경우, 응답을 생성할 수 없다.")
    void failsBuildWithoutStatusCode() {
        HttpResponseBuilder builder = new HttpResponseBuilder();
        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Status code is not set");
    }
}
