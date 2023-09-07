package nextstep.org.apache.coyote.http11;

import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.controller.LoginController;
import org.junit.jupiter.api.Test;

class HandlerMapperTest {

    private HandlerMapper handlerMapper = new HandlerMapper();

    @Test
    void mapHandlerByUrlTest() {
        // given
        String requestedUrl = "/login";

        // when
        LoginController loginController = (LoginController) handlerMapper.mapHandler(requestedUrl);

        // then
        assertNotNull(loginController);
    }
}
