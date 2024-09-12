package org.apache.catalina.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.apache.catalina.mvc.Controller;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techcourse.controller.RootController;

class RequestMappingTest {


    @Nested
    @DisplayName("컨트롤러 조회")
    class getController {
        @Test
        @DisplayName("성공 : request에 대응하는 컨트롤러 조회")
        void getControllerSuccess() {
            RequestMapping requestMapping = new RequestMapping();
            HttpRequest request = new HttpRequest(
                    new RequestLine("GET / HTTP/1.1"), new RequestHeader(Map.of()), new RequestBody(Map.of()));

            Controller controller = requestMapping.getController(request);

            assertThat(controller).hasSameClassAs(new RootController());
        }

        @Test
        @DisplayName("실패 : request에 대응하는 컨트롤러가 없으면 예외 발생")
        void getControllerFail() {
            RequestMapping requestMapping = new RequestMapping();
            HttpRequest request = new HttpRequest(
                    new RequestLine("GET /hello HTTP/1.1"), new RequestHeader(Map.of()), new RequestBody(Map.of()));

            assertThatThrownBy(() -> requestMapping.getController(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("연결된 컨트롤러가 없습니다.");

        }
    }
}
