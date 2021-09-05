package nextstep.jwp.framework.mapper;

import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.controller.StaticResourceController;
import nextstep.jwp.framework.exception.UriMappingNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ControllerMapperTest {

    @DisplayName("정적 파일의 경우 StaticResourceController 를 resolve 한다.")
    @Test
    void resolveFromStaticPath() {
        ControllerMapper controllerMapper = ControllerMapper.getInstance();
        Controller controller = controllerMapper.resolve("/test.html");
        assertThat(controller).isInstanceOf(StaticResourceController.class);
    }

    @DisplayName("매핑 정보가 없으면 예외가 발생한다.")
    @Test
    void resolveWithUndefinedUri() {
        ControllerMapper controllerMapper = ControllerMapper.getInstance();
        assertThatThrownBy(() -> controllerMapper.resolve("/ggyool"))
                .isInstanceOf(UriMappingNotFoundException.class)
                .hasMessageContaining("해당 uri의 매핑을 찾을 수 없습니다");
    }
}
