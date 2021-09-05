package nextstep.jwp.framework.controller;

import nextstep.jwp.framework.message.request.HttpRequestMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.testutils.TestHttpRequestUtils.makeDummyRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractControllerTest {

    @DisplayName("컨트롤러가 doGet 을 오버라이딩 하지 않으면 UnsupportedOperationException 예외가 발생한다.")
    @Test
    void unsupportedDoGet() throws IOException {
        CustomClass customClass = new CustomClass();
        HttpRequestMessage dummyRequest = makeDummyRequest();
        assertThatThrownBy(() -> customClass.doGet(dummyRequest))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("GET을 지원하지 않습니다");
    }

    @DisplayName("컨트롤러가 doPost 를 오버라이딩 하지 않으면 UnsupportedOperationException 예외가 발생한다.")
    @Test
    void unsupportedDoPost() throws IOException {
        CustomClass customClass = new CustomClass();
        HttpRequestMessage dummyRequest = makeDummyRequest();
        assertThatThrownBy(() -> customClass.doPost(dummyRequest))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("POST을 지원하지 않습니다");
    }

    private static class CustomClass extends AbstractController {

    }
}
