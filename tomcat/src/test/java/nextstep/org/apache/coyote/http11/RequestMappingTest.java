package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.model.HttpRequestURI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.ui.IndexController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import nextstep.jwp.ui.ResourceController;

public class RequestMappingTest {

    @Test
    @DisplayName("주어진 경로에 맞는 컨트롤러를 반환한다.")
    void findController() {
        assertAll(
            () -> assertThat(mapController("/login")).isInstanceOf(LoginController.class),
            () -> assertThat(mapController("/index")).isInstanceOf(IndexController.class),
            () -> assertThat(mapController("/register")).isInstanceOf(RegisterController.class)
        );
    }

    @Test
    @DisplayName("정의되지 않은 경로에 대해 ResourceController를 반환한다.")
    void findResourceController() {
        assertAll(
            () -> assertThat(mapController("/")).isInstanceOf(ResourceController.class),
            () -> assertThat(mapController("/404")).isInstanceOf(ResourceController.class));
    }

    private Controller mapController(String path) {
        HttpRequestURI requestURI = HttpRequestURI.from(path);
        return RequestMapping.getController(requestURI);
    }
}
