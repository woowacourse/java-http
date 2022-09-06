package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.catalina.servlets.Controller;
import org.apache.catalina.servlets.ControllerMappings;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.spec.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerMappingsTest {

    private final Controller stubController = new Controller() {
        @Override
        public boolean isProcessable(HttpRequest request) {
            return request.isPathEqualTo("/test");
        }

        @Override
        public void service(HttpRequest request, HttpResponse response) {
        }
    };

    @Test
    @DisplayName("path를 입력받아 이를 처리할 수 있는 핸들러를 찾아 반환한다.")
    void getAdaptiveHandler() {
        ControllerMappings controllerMappings = new ControllerMappings(List.of(stubController));
        HttpRequest httpRequest = new HttpRequest(StartLine.from("GET /test HTTP/1.1"), null);

        Controller controller = controllerMappings.getAdaptiveController(httpRequest);

        assertThat(controller).isInstanceOf(Controller.class);
    }

    @Test
    @DisplayName("입력된 path를 처리할 핸들러가 없는 경우 null을 반환한다.")
    void returnNullIfNoHandler() {
        ControllerMappings controllerMappings = new ControllerMappings(List.of(stubController));
        HttpRequest httpRequest = new HttpRequest(StartLine.from("GET /no-handler HTTP/1.1"), null);

        Controller controller = controllerMappings.getAdaptiveController(httpRequest);

        assertThat(controller).isNull();
    }
}
