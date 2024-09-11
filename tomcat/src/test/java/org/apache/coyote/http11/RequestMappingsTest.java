package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.controller.AbstractController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingsTest {

    @Test
    @DisplayName("여러 RequestMapping 중 가장 먼저 매핑되는 Controller를 반환한다")
    void findController() {
        Controller firstController = new AbstractController() {
        };
        RequestMapping first = RequestMapping.from(firstController, "first");
        Controller secondController = new AbstractController() {
        };
        RequestMapping second = RequestMapping.from(secondController, "second", "first");

        RequestMappings requestMappings = new RequestMappings(first, second);

        Controller controller = requestMappings.findController("path/first");

        assertThat(controller)
                .isEqualTo(firstController);
    }

    @Test
    @DisplayName("메핑되는 컨트롤러가 없으면 예외를 던진다")
    void findControllerFail() {
        Controller firstController = new AbstractController() {
        };
        RequestMapping first = RequestMapping.from(firstController, "first");
        Controller secondController = new AbstractController() {
        };
        RequestMapping second = RequestMapping.from(secondController, "second", "first");

        RequestMappings requestMappings = new RequestMappings(first, second);

        assertThatThrownBy(() -> requestMappings.findController("path/not_found"));
    }
}
