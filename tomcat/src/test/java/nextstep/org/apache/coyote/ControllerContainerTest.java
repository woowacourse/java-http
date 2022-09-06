package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.Controller;
import org.apache.coyote.ControllerContainer;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.StaticFileController;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11URL;
import org.junit.jupiter.api.Test;

class ControllerContainerTest {

    @Test
    void findController() {
        // given
        final Http11Request request = new Http11Request(HttpMethod.GET, new Http11URL("/login.html"), null, null, null);

        // when
        final Controller actual = ControllerContainer.findController(request);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }
}
