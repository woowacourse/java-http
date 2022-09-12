package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestBuilder;

public class RequestMappingTest {

    @DisplayName("request에 맞는 컨트롤러를 실행한다.")
    @Test
    void from() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .requestLine("POST /login HTTP/1.1 ")
                .build();

        // when

        assertThat(RequestMapping.from(request)).isEqualTo(RequestMapping.LOGIN);
    }
}
