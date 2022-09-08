package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.ControllerContainer;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.controller.StaticFileController;
import org.apache.coyote.http11.Protocol;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.URL;
import org.junit.jupiter.api.Test;

class ControllerContainerTest {

    @Test
    void findController() {
        // given
        final Request request = new Request(HttpMethod.GET, Protocol.HTTP1_1, new URL("/login.html"), null, null, null);

        // when
        final Controller actual = ControllerContainer.findController(request);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }
}
