package org.apache.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.fixture.TestController;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.mvc.handlerchain.RequestHandlerMethod;
import org.apache.mvc.handlerchain.RequestKey;
import org.junit.jupiter.api.Test;

class ControllerParserTest {

    @Test
    void parseControllerToHandlerMethod() {
        // given
        TestController controller = new TestController();
        Map<RequestKey, RequestHandlerMethod> map = ControllerParser.parse(List.of(controller));

        RequestKey requestKey = new RequestKey(RequestMethod.GET, "/");
        // when
        RequestHandlerMethod handlerMethod = map.get(requestKey);
        // then
        assertThat(handlerMethod).isNotNull();
    }
}
