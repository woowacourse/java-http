package nextstep.jwp.presentation.handler;

import nextstep.jwp.presentation.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;

class FrontControllerTest {

    @Test
    void findRootController() {
        //given
        FrontController frontController = new FrontController();

        //when
        Controller controller = frontController.findController(GET, "/");

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(RootController.class);
    }

    @Test
    void findGetLoginController() {
        //given
        FrontController frontController = new FrontController();

        //when
        Controller controller = frontController.findController(GET, "/login");

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(GetLoginController.class);
    }

    @Test
    void findGetRegisterController() {
        //given
        FrontController frontController = new FrontController();

        //when
        Controller controller = frontController.findController(GET, "/register");

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(GetRegisterController.class);
    }

    @Test
    void findStaticControllerWithCss() {
        //given
        FrontController frontController = new FrontController();

        //when
        Controller controller = frontController.findController(GET, "/css/styles.css");

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(StaticController.class);
    }

    @Test
    void findStaticControllerWithHTML() {
        //given
        FrontController frontController = new FrontController();

        //when
        Controller controller = frontController.findController(GET, "/index.html");

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(StaticController.class);
    }

    @Test
    void findPostLoginController() {
        //given
        FrontController frontController = new FrontController();

        //when
        Controller controller = frontController.findController(POST, "/login");

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(PostLoginController.class);
    }

    @Test
    void findPostRegisterController() {
        //given
        FrontController frontController = new FrontController();

        //when
        Controller controller = frontController.findController(POST, "/register");

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(PostRegisterController.class);
    }
}
