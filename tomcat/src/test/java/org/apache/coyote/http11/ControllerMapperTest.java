package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.mvc.Controller;
import org.apache.mvc.ControllerMapper;
import org.apache.mvc.annotation.RequestMapping;
import org.junit.jupiter.api.Test;

class ControllerMapperTest {

    public static class TestController implements Controller {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public ResponseEntity myMethod(HttpRequest httpRequest) {
            return new ResponseEntity(HttpStatus.OK, "hello");
        }
    }

    @Test
    void createMapperByController() {
        // given & when
        TestController controller = new TestController();
        // then
        assertThatNoException().isThrownBy(() -> ControllerMapper.from(List.of(controller)));
    }

    @Test
    void mapRequestToHandler() {
        // given
        TestController controller = new TestController();
        ControllerMapper mapper = ControllerMapper.from(List.of(controller));

        String http = String.join("\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        // when

        ResponseEntity responseEntity = mapper.mapToHandle(
                HttpRequest.parse(new ByteArrayInputStream(http.getBytes())));

        // then
        assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.OK);
    }
}
