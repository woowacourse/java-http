package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.ControllerContainer;
import nextstep.jwp.controller.requestmapping.JwpRequestMapping;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class ControllerContainerTest {

    @Test
    void findController() throws IOException {
        // given
        final String requestLine = RequestFixture.createLine(HttpMethod.GET, "/login.html", "");
        final InputStream inputStream = new ByteArrayInputStream(requestLine.getBytes());
        final Request request = Request.of(new BufferedReader(new InputStreamReader(inputStream)));

        // when
        final Controller actual =
                new ControllerContainer(JwpRequestMapping.getInstance(), List.of())
                        .findController(request);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }
}
