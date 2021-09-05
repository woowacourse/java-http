package nextstep.jwp.framework.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpRequestUtils.makeDummyRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractControllerTest {

    @DisplayName("컨트롤러가 doGet 을 오버라이딩 하지 않으면 UnsupportedOperationException 예외가 발생한다.")
    @Test
    void unsupportedDoGet() {
        CustomClass customClass = new CustomClass();
        assertThatThrownBy(() -> customClass.doGet(makeDummyRequest()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("GET을 지원하지 않습니다");
    }

    @DisplayName("컨트롤러가 doPost 를 오버라이딩 하지 않으면 UnsupportedOperationException 예외가 발생한다.")
    @Test
    void unsupportedDoPost() {
        CustomClass customClass = new CustomClass();
        assertThatThrownBy(() -> customClass.doPost(makeDummyRequest()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("POST을 지원하지 않습니다");
    }

    private static class CustomClass extends AbstractController {

    }
}
