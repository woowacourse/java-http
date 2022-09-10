package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.ui.Controller;
import org.apache.coyote.http11.request.Path;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    void 없는_api_path_요청일_경우_예외를_반환한다() {
        // given
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/eden", (request, response) -> System.out.println());

        // when & then
        assertThatThrownBy(() -> RequestMapping.findController(Path.fromPath("/king")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
