package org.apache.coyote.support;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nextstep.jwp.presentation.HomeController;
import org.apache.coyote.http11.http.HttpPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    @DisplayName("요청경로로 해당 컨트롤러를 조회한다.")
    void getController() {
        RequestMapping requestMapping = new RequestMapping();

        Controller controller = requestMapping.getController(new HttpPath("/index.html"));

        assertThat(controller).isInstanceOf(HomeController.class);
    }
}