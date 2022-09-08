package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.ui.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    void 없는_api_path_요청일_경우_예외를_반환한다() {
        // given
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/eden", (request, response) -> System.out.println());

        // when & then
        assertThatThrownBy(() -> RequestMapping.getController(Path.fromPath("/king")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
