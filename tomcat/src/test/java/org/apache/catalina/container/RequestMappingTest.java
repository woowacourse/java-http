package org.apache.catalina.container;

import jakarta.controller.Controller;
import jakarta.http.Header;
import jakarta.http.HttpBody;
import jakarta.http.HttpRequest;
import jakarta.http.HttpSessionWrapper;
import jakarta.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class RequestMappingTest {

    @Test
    @DisplayName("controller map은 null을 허용하지 않는다.")
    void createWithControllerMapNull() {
        assertThatThrownBy(() -> new RequestMapping(null, mock(Controller.class)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("defaultController는 null을 허용하지 않는다.")
    void createWithDefaultControllerNull() {
        assertThatThrownBy(() -> new RequestMapping(Collections.emptyMap(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("찾으려는 controller가 존재하지 않는다면, default를 반환한다.")
    void getController() {
        Controller mockController = mock(Controller.class);
        RequestMapping requestMapping = new RequestMapping(Collections.emptyMap(), mockController);
        HttpRequest request = HttpRequest.createHttpRequest(
                "GET / HTTP/1.1",
                Header.empty(),
                mock(HttpBody.class),
                HttpVersion.HTTP_1_1,
                mock(HttpSessionWrapper.class)
        );

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isSameAs(mockController);
    }
}
