package nextstep.jwp.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ControllerMapperTest {


    @DisplayName("path 와 매칭된 controller 가 정상 반환된다.")
    @ParameterizedTest(name = "{index} => path={0}, controller={1}")
    @MethodSource("pathAndControllerParams")
    void findController(String path, Handler controller) {
        // when
        final Handler handler = ControllerMapper.findController(path);

        // then
        Assertions.assertEquals(handler, controller);
    }

    private static Object[] pathAndControllerParams() {
        return new Object[]{
                new Object[]{"/", RootController.getINSTANCE()},
                new Object[]{"/login", LoginController.getINSTANCE()},
                new Object[]{"/register", RegisterController.getINSTANCE()},
                new Object[]{"/css/style.css", ResourceController.getINSTANCE()},
                new Object[]{"/js/scripts.js", ResourceController.getINSTANCE()},
                new Object[]{"/assets/chart-area.js", ResourceController.getINSTANCE()},
                new Object[]{"/assets/img/error-404-monochrome.svg",
                        ResourceController.getINSTANCE()},
        };
    }
}
