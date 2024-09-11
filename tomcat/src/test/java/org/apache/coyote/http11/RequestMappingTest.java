package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.AbstractController;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    @DisplayName("여러 경로 중 하나를 만족하는 경우 해당되는 컨트롤러를 반환한다")
    void getControllerIfMapped() {
        AbstractController controller = new AbstractController() {
        };
        RequestMapping requestMapping = RequestMapping.from(controller, "path1", "path2");

        Optional<Controller> result = requestMapping.getControllerIfMapped("/alpha/path1/beta");

        assertThat(result.orElseThrow(() -> new RuntimeException("매핑된 컨트롤러가 없습니다. 메서드 구현을 확인해 주세요.")))
                .isEqualTo(controller);
    }

    @Test
    @DisplayName("여러 경로 중 하나도 만족하지 못하면 비어있는 Optional을 반환한다.")
    void getControllerIfMappedWhenNotMatch() {
        AbstractController controller = new AbstractController() {
        };
        RequestMapping requestMapping = RequestMapping.from(controller, "path1", "path2");

        Optional<Controller> result = requestMapping.getControllerIfMapped("/alpha/path456/beta");

        assertThat(result).isEmpty();
    }
}
