package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceHandlerTest {

    @Test
    @DisplayName("입력받은 정적 리소스에 대한 디렉토리 경로를 찾을 수 있다.")
    void handle1() {
        ResourceHandler resourceHandler = new StaticResourceHandler("/hello.html");

        String result = resourceHandler.handle();

        assertThat(result).isEqualTo("static/hello.html");
    }

    @Test
    @DisplayName("빈 요청이 왔을 때 /index.html에 해당하는 디렉토리 경로를 찾을 수 있다.")
    void handle2() {
        ResourceHandler resourceHandler = new StaticResourceHandler("/");

        String result = resourceHandler.handle();

        assertThat(result).isEqualTo("static/index.html");
    }
}
