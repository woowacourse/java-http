package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.ui.FileController;
import nextstep.jwp.ui.HomeController;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.request.Path;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    void 없는_api_path_요청일_경우_파일_컨트롤러를_반환한다() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.registerController("/eden", new HomeController());
        requestMapping.setFileController(new FileController());

        // when
        Controller controller = requestMapping.findController(Path.fromPath("/king"));

        // then
        assertThat(controller).isInstanceOf(FileController.class);
    }
}
