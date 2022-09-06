package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.Controller;
import org.apache.coyote.ControllerContainer;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.StaticFileController;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.URL;
import org.junit.jupiter.api.Test;

class ControllerContainerTest {

    @Test
    void findController() {
        // given
        final Request request = new Request(HttpMethod.GET, new URL("/login.html"), null, null, null);

        // when
        final Controller actual = ControllerContainer.findController(request);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }
}
