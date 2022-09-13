package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.handler.Controller;
import org.apache.coyote.handler.ResourceHandler;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

    @Test
    void uri에_해당하는_Controller를_반환한다() {
        RequestMapper requestMapper = new RequestMapper();
        Controller controller = requestMapper.find("/login");
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void uri에_해당하는_Controller가_없으면_ResourceHandler를_반환한다() {
        RequestMapper requestMapper = new RequestMapper();
        Controller controller = requestMapper.find("None");
        assertThat(controller).isInstanceOf(ResourceHandler.class);
    }
}
