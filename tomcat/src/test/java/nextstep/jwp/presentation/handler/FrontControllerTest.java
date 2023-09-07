package nextstep.jwp.presentation.handler;

import coyote.http.RequestFixture;
import nextstep.jwp.presentation.*;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class FrontControllerTest {

    @Test
    void findRootController() throws IOException {
        //given
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = createHttpRequest(RequestFixture.ROOT_REQUEST);

        //when
        Controller controller = frontController.handle(httpRequest);

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(RootController.class);
    }

    private HttpRequest createHttpRequest(String request) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        return httpRequestParser.convertToHttpRequest(inputStream);
    }

    @Test
    void findGetLoginController() throws IOException {
        //given
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = createHttpRequest(RequestFixture.GET_LOGIN_REQUEST);

        //when
        Controller controller = frontController.handle(httpRequest);

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(LoginController.class);
    }

    @Test
    void findGetRegisterController() throws IOException {
        //given
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = createHttpRequest(RequestFixture.GET_REGISTER_REQUEST);

        //when
        Controller controller = frontController.handle(httpRequest);

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(RegisterController.class);
    }

    @Test
    void findStaticControllerWithCss() throws IOException {
        //given
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = createHttpRequest(RequestFixture.CSS_REQUEST);

        //when
        Controller controller = frontController.handle(httpRequest);

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(StaticController.class);
    }

    @Test
    void findStaticControllerWithHTML() throws IOException {
        //given
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = createHttpRequest(RequestFixture.HTML_REQUEST);

        //when
        Controller controller = frontController.handle(httpRequest);

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(StaticController.class);
    }

    @Test
    void findPostLoginController() throws IOException {
        //given
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = createHttpRequest(RequestFixture.POST_SUCCESS_LOGIN_REQUEST);

        //when
        Controller controller = frontController.handle(httpRequest);

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(LoginController.class);
    }

    @Test
    void findPostRegisterController() throws IOException {
        //given
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = createHttpRequest(RequestFixture.POST_REGISTER_LOGIN_REQUEST);

        //when
        Controller controller = frontController.handle(httpRequest);

        //then
        Assertions.assertThat(controller.getClass()).isEqualTo(RegisterController.class);
    }
}
