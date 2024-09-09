package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    @Test
    @DisplayName("빈 요청이 왔을 때 기본 파일이름을 반환한다.")
    void process() {
        StaticResourceController controller = new StaticResourceController();

        String result = controller.process("/");

        assertThat(result).isEqualTo("/index.html");
    }
}
