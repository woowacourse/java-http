package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    void 요청_경로에_등록된_Controller를_반환한다() {
        Controller loginController = new TestController();
        Controller defaultController = new TestController();

        RequestMapping mapping = new RequestMapping(defaultController);
        mapping.register("/login", loginController);

        assertThat(mapping.getController("/login"))
                .isSameAs(loginController);
    }

    @Test
    void 등록되지_않은_요청에는_기본_Controller를_반환한다() {
        Controller defaultController = new TestController();

        RequestMapping mapping = new RequestMapping(defaultController);

        assertThat(mapping.getController("/unknown"))
                .isSameAs(defaultController);
    }

    private static class TestController
            extends AbstractController {
    }
}
