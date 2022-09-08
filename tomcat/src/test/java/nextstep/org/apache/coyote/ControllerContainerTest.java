package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.ControllerContainer;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.Protocol;
import org.apache.coyote.http11.URL;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.junit.jupiter.api.Test;

class ControllerContainerTest {

    @Test
    void findController() {
        // given
        final Request request = new Request(HttpMethod.GET, Protocol.HTTP1_1, new URL("/login.html"), null, null, null);

        // when
        final Controller actual =
                new ControllerContainer(new RequestMapping(), List.of())
                        .findController(request);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }
}
