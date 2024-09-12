package org.apache.coyote.http;

import com.techcourse.controller.TestController;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.response.ResponseHeader;
import org.apache.coyote.http.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DispatcherTest {

    static class TestDevController extends AbstractController {

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
            response.setResponse(new StatusLine(HttpStatus.OK), new ResponseHeader(), "GET /dev");
        }
    }

    @Test
    @DisplayName("요청에 따라 적절한 컨트롤러를 선택한다.")
    void dispatch() {
        Map<String, Controller> controllers = Map.of(
                "/test", new TestController(),
                "/dev", new TestDevController()
        );
        Dispatcher dispatcher = new Dispatcher(controllers);

        HttpRequest getTestRequest = HttpRequest.of("GET /test HTTP/1.1");
        HttpRequest getDevRequest = HttpRequest.of("GET /dev HTTP/1.1");
        HttpResponse response = new HttpResponse();

        dispatcher.dispatch(getTestRequest, response);
        assertThat(response.toResponse()).contains("GET body");
        dispatcher.dispatch(getDevRequest, response);
        assertThat(response.toResponse()).contains("GET /dev");
    }
}
