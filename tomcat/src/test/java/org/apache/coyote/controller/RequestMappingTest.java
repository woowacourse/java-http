package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.coyote.request.MyHttpRequest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class RequestMappingTest {

    @Test
    void 전달받은_매핑을_복사해_경로에_맞는_컨트롤러를_반환한다() {
        Controller mappedController = mock(Controller.class);
        Controller fallbackController = mock(Controller.class);
        Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/login", mappedController);
        RequestMapping requestMapping = new RequestMapping(controllers, fallbackController);
        controllers.clear();

        MyHttpRequest request = mock(MyHttpRequest.class);
        when(request.getPath()).thenReturn("/login");

        assertThat(requestMapping.getController(request)).isSameAs(mappedController);
    }

    @Test
    void 매핑되지_않은_경로에는_기본_컨트롤러를_반환한다() {
        Controller fallbackController = mock(Controller.class);
        RequestMapping requestMapping = new RequestMapping(Map.of(), fallbackController);
        MyHttpRequest request = mock(MyHttpRequest.class);
        when(request.getPath()).thenReturn("/unknown");

        assertThat(requestMapping.getController(request)).isSameAs(fallbackController);
    }
}
